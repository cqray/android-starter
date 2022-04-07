package cn.cqray.android.app;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;
import java.util.Stack;
import java.util.UUID;

import cn.cqray.android.Starter;
import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.exception.ExceptionManager;
import cn.cqray.android.lifecycle.LifecycleViewModel;
import cn.cqray.android.lifecycle.LifecycleViewModelProvider;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Fragment启动器ViewModel，依附于Activity
 * <P>本来想实现可依附于Fragment，但是会层层嵌套，导致逻辑变复杂，不利于排错</p>
 * <p>所以简化逻辑，只能依附于Activity，通过{@link LifecycleViewModelProvider}获取</P>
 * @author Cqray
 */
@Accessors(prefix = "m")
public final class StarterViewModel extends LifecycleViewModel {
    /** id关键字 **/
    private static final String FRAGMENT_ID_KEY = "starter:fragment_id";
    /** 容器Id **/
    private @Getter int mContainerId;
    /** 回退栈 **/
    private final Stack<String> mBackStack = new Stack<>();

    public StarterViewModel(@NonNull LifecycleOwner owner) {
        super(owner);
        if (!(owner instanceof FragmentActivity) || !(owner instanceof StarterProvider)) {
            throw new RuntimeException("StarterViewModel can only get by FragmentActivity which implements StarterProvider.");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mBackStack.clear();
    }

    @NonNull
    public FragmentActivity requireActivity() {
        return (FragmentActivity) getLifecycleOwner();
    }

    @NonNull
    public FragmentManager getFragmentManager() {
        return requireActivity().getSupportFragmentManager();
    }

    /**
     * 回退实现
     */
    public void onBackPressed() {
        Fragment fragment = getTopFragment();
        // 栈顶元素为空，说明没有调用LoadRootFragment。
        if (fragment == null) {
            // 因为LifecycleOwner即Activity
            // 所有获取拦截结果，决定是否回退
            StarterProvider provider = (StarterProvider) getLifecycleOwner();
            if (!provider.onBackPressedSupport()) {
                pop();
            }
            return;
        }
        // 栈顶Fragment不为空，回退栈顶Fragment
        // 判断是否进行回退拦截
        if (mBackStack.size() > 1) {
            // 如果回退栈的数量大于1，则仅需判断当前Fragment的回退拦截
            if (!((StarterProvider) fragment).onBackPressedSupport()) {
                pop();
            }
        } else {
            // 如果回退栈的数量为1，则还需判断父级回退拦截
            if (!((StarterProvider) fragment).onBackPressedSupport()) {
                // 如果Fragment回退未被拦截，则传递给父级
                StarterProvider provider = (StarterProvider) getLifecycleOwner();
                if (!provider.onBackPressedSupport()) {
                    pop();
                }
            }
        }
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param intent intent对象
     */
    @NonNull
    public Fragment generateFragment(@NonNull NavIntent intent) {
        // Fragment工厂
        FragmentFactory factory = Objects.requireNonNull(getFragmentManager()).getFragmentFactory();
        // 类加载器
        ClassLoader loader = requireActivity().getClassLoader();
        // 获取参数
        Bundle arguments = intent.getArguments();
        // 设置ID
        arguments.putString(FRAGMENT_ID_KEY, UUID.randomUUID().toString().replace("-", ""));
        // 创建Fragment
        Fragment fragment = factory.instantiate(loader, intent.getToClass().getName());
        // 设置参数
        fragment.setArguments(arguments);
        // 返回Fragment
        return fragment;
    }

    /**
     * 获取回退栈栈顶的Fragment
     */
    @Nullable
    public Fragment getTopFragment() {
        if (mBackStack.isEmpty()) {
            return null;
        }
        return getFragmentManager().findFragmentByTag(mBackStack.peek());
    }

    /**
     * 获取Fragment对应的标识
     * @param fragment fragment对象
     */
    @NonNull
    public String getFragmentTag(@NonNull Fragment fragment) {
        Bundle arguments = fragment.getArguments();
        String id = arguments == null ? "" : arguments.getString(FRAGMENT_ID_KEY);
        return fragment.getClass().getName() + "-" + id;
    }

    /**
     * 是否是最先加载的Fragment
     * @param cls fragment对应class
     */
    public boolean isRootFragment(Class<?> cls) {
        if (mBackStack.isEmpty()) {
            return false;
        }
        String backStackName = mBackStack.firstElement();
        return backStackName.split("-")[0].equals(cls.getName());
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent NavIntent
     */
    public void loadRootFragment(@IdRes int containerId, NavIntent intent) {
        mContainerId = containerId;
        start(intent);
    }

    /**
     * 启动Fragment
     * @param intent NavIntent
     */
    public void start(@NonNull NavIntent intent) {
        if (mContainerId == 0) {
            // 还未设置根Fragment
            ExceptionManager.getInstance().showException(new Exception());
            return;
        }
        // 是否需要回退
        boolean needPop = intent.getPopToClass() != null;
        // 回退到指定的Fragment
        if (needPop) {
            popTo(intent.getPopToClass(), intent.isPopToInclusive());
        }
        // 获取栈顶Fragment
        Fragment top = getTopFragment();
        if (top != null
                && intent.isSingleTop()
                && top.getClass().equals(intent.getToClass())) {
            // 连续两个Fragment不重复
            return;
        }
        // 获取Fragment管理器
        FragmentManager fm = getFragmentManager();
        // 获取Fragment事务
        FragmentTransaction ft = fm.beginTransaction();
        // 生成Fragment
        Fragment fragment = generateFragment(intent);
        // 如果回退栈不为空，则需要显示动画
        if (!mBackStack.isEmpty()) {
            // 获取并设置动画
            FragmentAnimator fa = getFragmentAnimator(fragment, intent);
            ft.setCustomAnimations(fa.mEnter, fa.mExit, fa.mPopEnter, fa.mPopExit);
        }
        // 隐藏当前正在显示的Fragment
        Fragment current = fm.getPrimaryNavigationFragment();
        if (current != null) {
            ft.setMaxLifecycle(current, Lifecycle.State.STARTED);
            if (intent.getPopToClass() != null) {
                ft.hide(current);
            }
        }
        // 获取FragmentTag
        String fragmentTag = getFragmentTag(fragment);
        // 添加Fragment
        ft.add(mContainerId, fragment, fragmentTag);
        // 设置初始化生命周期
        ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
        // 设置当前Fragment
        ft.setPrimaryNavigationFragment(fragment);
        // 加入BackStack
        ft.addToBackStack(fragmentTag);
        // 加入回退栈
        mBackStack.add(fragmentTag);
        // 提交事务
        ft.setReorderingAllowed(true);
        ft.commit();
    }

    /**
     * 回退到指定的Fragment
     * @param popTo 指定的Fragment
     * @param inclusive 是否包含指定的Fragment
     */
    public void popTo(Class<?> popTo, boolean inclusive) {
        FragmentManager fm = getFragmentManager();
        // 如果是第一个入栈的Fragment并且需要销毁，则父级pop。
        if (isRootFragment(popTo) && inclusive) {
            requireActivity().finish();
            return;
        }
        // 操作当前Fragment回退栈中的Fragment
        boolean needContinue = true;
        for (int i = 0; i < mBackStack.size(); i++) {
            String fragmentTag = mBackStack.get(i);
            if (fragmentTag.split("-")[0].equals(popTo.getName())) {
                if (inclusive) {
                    fm.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    popBackStackAfter(i);
                    return;
                } else {
                    needContinue = false;
                    continue;
                }
            }
            if (!needContinue) {
                fm.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                popBackStackAfter(i);
                return;
            }
        }
    }

    /**
     * 回退Fragment
     */
    public void pop() {
        if (mBackStack.size() <= 1) {
            requireActivity().finish();
            return;
        }
        FragmentManager fm = getFragmentManager();
        if (!fm.isStateSaved()) {
            // Fragment回退
            fm.popBackStack(mBackStack.pop(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * 回退栈从指定位置后出栈
     * @param index 指定位置
     */
    private void popBackStackAfter(int index) {
        if (mBackStack.size() > index) {
            mBackStack.subList(index, mBackStack.size()).clear();
        }
    }

    /**
     * 获取Fragment动画
     * @param fragment 动画作用的Fragment
     * @param intent 启动传参
     */
    @NonNull
    private FragmentAnimator getFragmentAnimator(@NonNull Fragment fragment, @NonNull NavIntent intent) {
        // 优先集最高的Fragment动画
        FragmentAnimator fa = intent.getFragmentAnimator();
        // Fragment自定义动画
        if (fa == null) {
            fa = ((StarterProvider) fragment).onCreateFragmentAnimator();
        }
        // 父级自定义动画
        if (fa == null) {
            fa = ((StarterProvider) getLifecycleOwner()).onCreateFragmentAnimator();
        }
        // 全局默认的动画
        if (fa == null) {
            fa = Starter.getInstance().getFragmentAnimator();
        }
        return fa;
    }
}
