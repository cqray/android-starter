package cn.cqray.android.app2

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.app.GetIntent
import cn.cqray.android.exception.ExceptionDispatcher
import cn.cqray.android.exception.ExceptionType
import java.util.*

class GetDelegate {


    lateinit var navViewModel: GetNavViewModel

    private lateinit var provider: GetProvider

    /** Handler对象  */
    private val handler = Handler(Looper.getMainLooper())

    constructor(provider: GetProvider) {
        this.provider = provider
        delegates[provider] = this
    }

//    init {
//        if (provider is AppCompatActivity || provider is Fragment) {
//            this.provider = provider
//        } else {
//            throw RuntimeException(
//                String.format(
//                    "%s must extends %s or %s.",
//                    provider.javaClass.name,
//                    AppCompatActivity::class.java.name,
//                    Fragment::class.java.name
//                )
//            )
//        }
//    }


    fun onCreated() {

        if (provider is Fragment) {
            val fragment = provider as Fragment
            val activity = fragment.requireActivity()
            navViewModel = GetViewModelProvider(activity).get(GetNavViewModel::class.java)
        } else {
            val activity = provider as FragmentActivity
            activity.onBackPressedDispatcher.addCallback(
                activity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        navViewModel.onBackPressed()
                    }
                })
            navViewModel = GetViewModelProvider(activity).get(GetNavViewModel::class.java)
        }

    }

    fun onViewCreated() {
//        val enterAnimDuration: Int
//        enterAnimDuration = if (provider is Fragment) {
//            // 获取Fragment进入时的动画时长
//            viewModel.getAnimDuration()
//        } else {
//            // 获取Activity进入时的动画时长
//            val animResId =
//                SupportUtils.getActivityOpenEnterAnimationResId((provider as Activity?)!!)
//            SupportUtils.getAnimDurationFromResource(animResId)
//        }
//        // 进入动画结束回调
//        // 进入动画结束回调
//        handler.postDelayed(Runnable { provider.onEnterAnimEnd() }, enterAnimDuration.toLong())
    }

    fun onDestroyed() {
        handler.removeCallbacksAndMessages(null)
        delegates.remove(provider)
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent) = navViewModel.loadRootFragment(containerId, intent)

    /**
     * 启动Fragment
     * @param intent NavIntent
     */
    fun start(intent: GetIntent) = navViewModel.start(intent)

    /**
     * 回退到指定的Fragment
     * @param popTo 指定的Fragment
     * @param inclusive 是否包含指定的Fragment
     */
    fun popTo(popTo: Class<*>?, inclusive: Boolean) {
//        if (isViewModelReady()) {
//            navViewModel.popTo(popTo, null, inclusive)
//        }
    }

    fun canPop(): Boolean {
        //return mStarterCache.getBackStackCount() > 1;
        return false
    }

    /**
     * 界面回退
     */
    fun pop() {
//        if (isViewModelReady()) {
//            navViewModel.pop()
//        }
    }

    fun getContainerView(): View? {
        return navViewModel.containerView
    }

//    /**
//     * ViewModel是否准备完毕
//     */
//    private fun isViewModelReady(): Boolean {
//        if (navViewModel == null) {
//            ExceptionDispatcher.dispatchThrowable(provider, ExceptionType.STARTER_ILLEGAL_STATE)
//            return false
//        }
//        return true
//    }

    /** 委托缓存  */


    companion object {

        // GetDelegate缓存
        private var delegates: MutableMap<GetProvider, GetDelegate> = Collections.synchronizedMap(HashMap())

        @JvmStatic
        fun get(provider: GetProvider): GetDelegate {
            var delegate = delegates[provider]
            if (delegate == null) {
                delegate = GetDelegate(provider)
                delegates[provider] = delegate
            }
            return delegate
        }

//        private fun putCache(provider: GetProvider) = delegates[provider]
//
//        private fun release(provider: GetProvider) = delegates.remove(provider)
    }

//    private object Holder {
//
//    }
}