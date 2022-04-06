package cn.cqray.android.app;

import android.app.Activity;
import android.content.Intent;
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

import com.blankj.utilcode.util.ActivityUtils;

import java.util.Objects;
import java.util.Stack;
import java.util.UUID;

import cn.cqray.android.Starter;
import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.exception.ExceptionManager;
import cn.cqray.android.lifecycle.LifecycleViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public final class StarterViewModel extends LifecycleViewModel {

    /** id关键字 **/
    private static final String FRAGMENT_ID_KEY = "starter:fragment_id";
    /** 容器Id **/
    @Getter
    private int mContainerId;
    /** 回退栈 **/
    private final Stack<String> mBackStack = new Stack<>();

    public StarterViewModel(@NonNull LifecycleOwner owner) {
        super(owner);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mBackStack.clear();
    }

    @Nullable
    public FragmentActivity getActivity() {
        LifecycleOwner owner = getLifecycleOwner();
        if (owner instanceof FragmentActivity) {
            return (FragmentActivity) owner;
        } else if (owner instanceof Fragment) {
            return ((Fragment) owner).requireActivity();
        }
        return null;
    }

    @Nullable
    public FragmentManager getFragmentManager() {
        LifecycleOwner owner = getLifecycleOwner();
        if (owner instanceof FragmentActivity) {
            return ((FragmentActivity) owner).getSupportFragmentManager();
        } else if (owner instanceof Fragment) {
            return ((Fragment) owner).getParentFragmentManager();
        }
        return null;
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param intent intent对象
     */
    @NonNull
    public Fragment generateFragment(@NonNull NavIntent intent) {
        if (getActivity() == null) {
            throw new NullPointerException("Do you initialize in Fragment or FragmentActivity?");
        }
        // Fragment工厂
        FragmentFactory factory = Objects.requireNonNull(getFragmentManager()).getFragmentFactory();
        // 类加载器
        ClassLoader loader = getActivity().getClassLoader();
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
        FragmentManager fm = getFragmentManager();
        if (fm == null || mBackStack.isEmpty()) {
            return null;
        }
        return fm.findFragmentByTag(mBackStack.peek());
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
            //popTo(intent.getPopToClass(), intent.isPopToInclusive());
        }
        // 如果toClass是Activity, 则直接操作Activity
        if (Activity.class.isAssignableFrom(intent.getToClass())) {
            Activity activity = ActivityUtils.getTopActivity();
            Intent actIntent = new Intent(activity, intent.getToClass());
            actIntent.putExtras(intent.getArguments());
            activity.startActivity(actIntent);
            return;
        }
        startFragment(intent);
    }

    public void startFragment(NavIntent intent) {
        Fragment top = getTopFragment();
        if (top != null
                && intent.isSingleTop()
                && top.getClass().equals(intent.getToClass())) {
            // 连续两个Fragment不重复
            return;
        }
        // 获取Fragment管理器
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return;
        }
        // 获取Fragment事务
        FragmentTransaction ft = fm.beginTransaction();
        // 生成Fragment
        Fragment fragment = generateFragment(intent);
        // 获取动画
        FragmentAnimator fa = intent.getFragmentAnimator() == null
                ? Starter.getInstance().getFragmentAnimator()
                : intent.getFragmentAnimator();
        //ft.addSharedElement();
        // 如果回退栈不为空，则需要显示动画
        if (!mBackStack.isEmpty()) {
            // 设置动画
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
    @SuppressWarnings("unchecked")
    public void popTo(Class<?> popTo, boolean inclusive) {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return;
        }
        // 如果popTo是Activity, 则直接操作Activity
        if (Activity.class.isAssignableFrom(popTo)) {
            cn.cqray.android.util.ActivityUtils.popTo((Class<? extends Activity>) popTo, inclusive);
            return;
        }
        // 如果是第一个入栈的Fragment并且需要销毁，则父级pop。
        if (isRootFragment(popTo) && inclusive) {
            popParent();
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
     * 父级Fragment或Activity退出
     */
    public void popParent() {
        FragmentActivity act = getActivity();
        if (act != null) {
            act.finish();
        }
    }

    void popBackStackAfter(int index) {
        if (mBackStack.size() > index) {
            mBackStack.subList(index, mBackStack.size()).clear();
        }
    }
}
