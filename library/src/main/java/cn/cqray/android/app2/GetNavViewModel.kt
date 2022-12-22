package cn.cqray.android.app2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Starter
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.SupportProvider
import cn.cqray.android.app.ViewProvider
import cn.cqray.android.exception.ExceptionDispatcher
import com.blankj.utilcode.util.ActivityUtils
import lombok.Getter
import lombok.experimental.Accessors
import java.lang.Exception
import java.util.*

@Accessors(prefix = ["m"])
@Suppress("unused")
class GetNavViewModel(owner: LifecycleOwner) : GetViewModel(owner) {

    /** 容器Id  */
    private var containerId = 0

    /** 动画时长  */
    @Getter
    private var mAnimDuration = 0

    /** 回退栈  */
    private val backStack = Stack<String>()

    /** 持有当前ViewModel的Activity  */
    @SuppressLint("StaticFieldLeak")
    private val mActivity: AppCompatActivity


    override fun onCleared() {
        super.onCleared()
        backStack.clear()
    }

    /**
     * 回退实现
     */
    fun onBackPressed() {
        val fragment = getTopFragment()
        // 栈顶元素为空，说明没有调用LoadRootFragment。
        if (fragment == null) {
            // 当前Activity的忙碌状态处理
            if (mActivity is ViewProvider) {
                val delegate = (mActivity as ViewProvider).viewDelegate.stateDelegate
                if (delegate.isBusy && delegate.isBusyCancelable) {
                    delegate.setIdle()
                    return
                }
            }
            // 获取Activity拦截结果，决定是否回退
            val provider = lifecycleOwner as SupportProvider
            if (!provider.onBackPressedSupport()) {
                pop()
            }
            return
        }
        // 当前Fragment的忙碌状态处理
        if (fragment is ViewProvider) {
            val delegate = (fragment as ViewProvider).viewDelegate.stateDelegate
            if (delegate.isBusy && delegate.isBusyCancelable) {
                delegate.setIdle()
                return
            }
        }
        // 栈顶Fragment不为空，回退栈顶Fragment
        // 判断是否进行回退拦截
        if (backStack.size > 1) {
            // 如果回退栈的数量大于1，则仅需判断当前Fragment的回退拦截
            if (!(fragment as SupportProvider).onBackPressedSupport()) {
                pop()
            }
        } else {
            // 如果回退栈的数量为1，则还需判断父级回退拦截
            if (!(fragment as SupportProvider).onBackPressedSupport()) {
                // 如果Fragment回退未被拦截，则传递给父级(Activity)
                val provider = lifecycleOwner as SupportProvider
                if (!provider.onBackPressedSupport()) {
                    pop()
                }
            }
        }
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param intent intent对象
     */
    @Suppress("unused")
    fun generateFragment(intent: GetIntent): Fragment {
        // 生成UUID
        val uuid = UUID.randomUUID().toString().replace("-", "")
        // Fragment管理器
        val fragmentManager = mActivity.supportFragmentManager
        // Fragment工厂
        val factory = fragmentManager.fragmentFactory
        // 创建Fragment
        val fragment = factory.instantiate(mActivity.classLoader, intent.toClass.name)
        // 获取参数
        val arguments = intent.arguments
        // 设置ID
        arguments.putString(FRAGMENT_ID_KEY, uuid)
        // 设置参数
        fragment.arguments = arguments
        // 返回Fragment
        return fragment
    }

    @Suppress("unused")
    fun getTopFragment(): Fragment? {
        if (backStack.isEmpty()) {
            return null
        }
        return mActivity.supportFragmentManager.findFragmentByTag(backStack.peek())
    }

//    /**
//     * 获取回退栈栈顶的Fragment
//     */
//    val topFragment: Fragment?
//        get() = if (backStack.isEmpty()) {
//            null
//        } else mActivity.supportFragmentManager.findFragmentByTag(backStack.peek())

    /**
     * 获取Fragment对应的标识
     * @param fragment fragment对象
     */
    @Suppress("unused")
    fun getFragmentTag(fragment: Fragment): String {
        val arguments = fragment.arguments
        val id = if (arguments == null) "" else arguments.getString(FRAGMENT_ID_KEY)!!
        return fragment.javaClass.name + "-" + id
    }

    /**
     * 是否是最先加载的Fragment
     * @param cls fragment对应class
     */
    @Suppress("unused")
    fun isRootFragment(cls: Class<*>): Boolean {
        if (backStack.isEmpty()) {
            return false
        }
        val backStackName = backStack.firstElement()
        return backStackName.split("-").toTypedArray()[0] == cls.name
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent NavIntent
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent) {
        this.containerId = containerId
        start(intent)
    }

    /**
     * 启动Fragment
     * @param intent NavIntent
     */
    fun start(intent: GetIntent) {
        // 未设置布局ID
        if (containerId == 0) {
            ExceptionDispatcher.dispatchStarterThrowable(
                null,
                "请先调用loadRootFragment()。",
                "未设置ContainerId，便开始调用start()方法。"
            )
            return
        }
        // 未设置目标Class
        if (intent.toClass == null) {
            ExceptionDispatcher.dispatchStarterThrowable(
                null,
                "未设置目标Class。",
                "start()跳转界面时没设置目标Class。"
            )
            return
        }
        // 是否需要回退
        val needPop = intent.popToClass != null
        // 是否从未加载过
        val neverLoad = backStack.isEmpty()
        // 回退到指定的Fragment或Activity
        if (needPop) {
            popTo(intent.popToClass, intent.toClass, intent.isPopToInclusive)
        }
        // 跳转到指定Activity
        if (Activity::class.java.isAssignableFrom(intent.toClass)) {
            val actIntent = Intent(ActivityUtils.getTopActivity(), intent.toClass)
            actIntent.putExtras(intent.arguments)
            ActivityUtils.startActivity(actIntent)
            return
        }
        // 获取栈顶Fragment
        val top = getTopFragment()
        if (top != null && intent.isSingleTop && top.javaClass == intent.toClass) {
            // 连续两个Fragment不重复
            return
        }
        // 获取Fragment管理器
        val fm = mActivity.supportFragmentManager
        // 获取Fragment事务
        val ft = fm.beginTransaction()
        // 生成Fragment
        val fragment = generateFragment(intent)
        // 如果回退栈不为空，则需要显示动画
        if (!neverLoad) {
            // 获取并设置动画
            val fa = getFragmentAnimator(fragment, intent)
            ft.setCustomAnimations(fa.mEnter, fa.mExit, fa.mPopEnter, fa.mPopExit)
            // 计算动画时长
            mAnimDuration = SupportUtils.getAnimDurationFromResource(fa.mEnter)
        }
        // 隐藏当前正在显示的Fragment
        val current = fm.primaryNavigationFragment
        if (current != null) {
            ft.setMaxLifecycle(current, Lifecycle.State.STARTED)
            if (intent.popToClass != null) {
                ft.hide(current)
            }
        }
        // 获取FragmentTag
        val fragmentTag = getFragmentTag(fragment)
        // 添加Fragment
        ft.add(containerId, fragment, fragmentTag)
        // 设置初始化生命周期
        ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        // 设置当前Fragment
        ft.setPrimaryNavigationFragment(fragment)
        // 加入BackStack
        ft.addToBackStack(fragmentTag)
        try {
            // 提交事务
            ft.setReorderingAllowed(true)
            ft.commitNow()
            // 加入回退栈
            backStack.add(fragmentTag)
        } catch (ignore: Exception) {}
    }

    /**
     * 回退到指定的Fragment或Activity
     * @param popTo 指定回退的Fragment或Activity
     * @param to 需要启动的Fragment或Activity
     * @param inclusive 是否包含指定的Fragment或Activity
     */
    @Suppress("unused")
    fun popTo(popTo: Class<*>, to: Class<*>?, inclusive: Boolean) {
        val fm = mActivity.supportFragmentManager
        // 回退到指定的Activity
        if (Activity::class.java.isAssignableFrom(popTo)) {
            ActivityUtils.finishToActivity((popTo as Class<out Activity?>), inclusive)
            return
        }
        // 回退至根Fragment，同时无目标Fragment，则销毁Activity
        if (isRootFragment(popTo) && inclusive && to == null) {
            mActivity.finish()
            return
        }
        // 操作当前Fragment回退栈中的Fragment
        var needContinue = true
        for (i in backStack.indices) {
            val fragmentTag = backStack[i]
            if (fragmentTag.split("-").toTypedArray()[0] == popTo.name) {
                if (inclusive) {
                    fm.popBackStackImmediate(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    popBackStackAfter(i)
                    return
                } else {
                    needContinue = false
                    continue
                }
            }
            if (!needContinue) {
                fm.popBackStackImmediate(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                popBackStackAfter(i)
                return
            }
        }
    }

    /**
     * 回退Fragment
     */
    @Suppress("unused")
    fun pop() {
        if (backStack.size <= 1) {
            // 回退栈数量小于等于1，直接结束Activity
            mActivity.finish()
            return
        }
        // FragmentManager对象
        val fm = mActivity.supportFragmentManager
        // Fragment回退
        if (!fm.isStateSaved) {
            fm.popBackStackImmediate(backStack.pop(), FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    val containerView: View get() = mActivity.findViewById(containerId)

    /**
     * 回退栈从指定位置后出栈
     * @param index 指定位置
     */
    private fun popBackStackAfter(index: Int) {
        if (backStack.size > index) {
            backStack.subList(index, backStack.size).clear()
        }
    }

    /**
     * 获取Fragment动画
     * @param fragment 动画作用的Fragment
     * @param intent 启动传参
     */
    private fun getFragmentAnimator(fragment: Fragment, intent: GetIntent): FragmentAnimator {
        // 优先集最高的Fragment动画
        var fa = intent.fragmentAnimator
        // Fragment自定义动画
        if (fa == null) {
            fa = (fragment as SupportProvider).onCreateFragmentAnimator()
        }
        // 父级自定义动画
        if (fa == null) {
            fa = (lifecycleOwner as SupportProvider).onCreateFragmentAnimator()
        }
        // 全局默认的动画
        if (fa == null) {
            fa = Starter.getInstance().starterStrategy.fragmentAnimator
        }
        return fa
    }

    private companion object {
        /** Fragment ID 关键字 **/
        const val FRAGMENT_ID_KEY = "Get:fragmentId"
    }

//    const val FRAGMENT_ID_KEY = "Get:fragment_id"

    init {
        if (owner !is AppCompatActivity || owner !is GetProvider) {
            val exc = String.format(
                "%s can only get by FragmentActivity which implements %s.",
                GetNavViewModel::class.java.name,
                GetProvider::class.java.name
            )
            throw RuntimeException(exc)
        }
        mActivity = owner
    }
}