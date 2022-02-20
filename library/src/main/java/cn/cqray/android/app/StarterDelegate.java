package cn.cqray.android.app;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Map;

import cn.cqray.android.Starter;
import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.exception.ExceptionManager;
import cn.cqray.android.util.ActivityUtils;

/**
 * Fragment启动委托
 * @author Cqray
 */
public final class StarterDelegate {

    private StarterCache mStarterCache;
    private final LifecycleOwner mLifecycleOwner;
    private static final Map<LifecycleOwner, StarterDelegate> DELEGATE_MAP = new HashMap<>();

    @NonNull
    public static StarterDelegate get(@NonNull AppCompatActivity activity) {
        return get((LifecycleOwner) activity);
    }

    @NonNull
    public static StarterDelegate get(@NonNull Fragment fragment) {
        return get((LifecycleOwner) fragment);
    }

    @NonNull
    private static synchronized StarterDelegate get(@NonNull LifecycleOwner owner) {
        StarterDelegate fm = DELEGATE_MAP.get(owner);
        if (fm == null) {
            fm = new StarterDelegate(owner);
            DELEGATE_MAP.put(owner, fm);
        }
        return fm;
    }

    private StarterDelegate(LifecycleOwner owner) {
        mLifecycleOwner = owner;
        // 添加到缓存
        DELEGATE_MAP.put(owner, this);
    }

    void onCreated() {
        // 获取缓存代理
        mStarterCache = StarterCache.get(mLifecycleOwner);
        // Fragment还需做回退处理
        if (mLifecycleOwner instanceof Fragment) {
            Fragment fragment = ((Fragment) mLifecycleOwner);
            FragmentActivity activity = fragment.requireActivity();
            OnBackPressedDispatcher dispatcher = activity.getOnBackPressedDispatcher();
            dispatcher.addCallback(fragment, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            });
        }
    }

    void onDestroyed() {
        DELEGATE_MAP.remove(mLifecycleOwner);
        StarterCache.remove(mLifecycleOwner);
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent NavIntent
     */
    public void loadRootFragment(@IdRes int containerId, NavIntent intent) {
        mStarterCache.setContainerId(containerId);
        start(intent);
    }

    /**
     * 启动Fragment
     * @param intent NavIntent
     */
    public void start(@NonNull NavIntent intent) {
        if (mStarterCache.getContainerId() == 0) {
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
        // 如果toClass是Activity, 则直接操作Activity
        if (Activity.class.isAssignableFrom(intent.getToClass())) {
            Activity activity = requireActivity();
            Intent actIntent = new Intent(activity, intent.getToClass());
            actIntent.putExtras(intent.getArguments());
            activity.startActivity(actIntent);
            return;
        }
        startFragment(intent);
    }

    /**
     * 启动Fragment
     * @param intent 意图
     */
    void startFragment(NavIntent intent) {
        // 获取Fragment管理器
        FragmentManager fm = getFragmentManager();
        // 获取Fragment事务
        FragmentTransaction ft = fm.beginTransaction();
        // 生成Fragment
        Fragment fragment = mStarterCache.generateFragment(this, intent);
        // 获取动画
        FragmentAnimator fa = getFragmentAnimator(mLifecycleOwner, intent);
        // 如果回退栈不为空，则需要显示动画
        if (mStarterCache.getBackStackCount() != 0) {
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
        String fragmentTag = mStarterCache.getFragmentTag(fragment);
        // 添加Fragment
        ft.add(mStarterCache.getContainerId(), fragment, fragmentTag);
        // 设置初始化生命周期
        ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
        // 设置当前Fragment
        ft.setPrimaryNavigationFragment(fragment);
        // 加入BackStack
        ft.addToBackStack(fragmentTag);
        // 加入回退栈
        mStarterCache.addToBackStack(fragmentTag);
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
        // 如果popTo是Activity, 则直接操作Activity
        if (Activity.class.isAssignableFrom(popTo)) {
            ActivityUtils.popTo((Class<? extends Activity>) popTo, inclusive);
            return;
        }
        // 如果是第一个入栈的Fragment并且需要销毁，则父级pop。
        if (mStarterCache.isRootFragment(popTo) && inclusive) {
            popParent();
            return;
        }
        // 操作当前Fragment回退栈中的Fragment
        boolean needContinue = true;
        for (int i = 0; i < mStarterCache.getBackStackCount(); i++) {
            String fragmentTag = mStarterCache.getFragmentTag(i);
            if (fragmentTag.split("-")[0].equals(popTo.getName())) {
                if (inclusive) {
                    fm.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mStarterCache.popBackStackAfter(i);
                    return;
                } else {
                    needContinue = false;
                    continue;
                }
            }
            if (!needContinue) {
                fm.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mStarterCache.popBackStackAfter(i);
                return;
            }
        }
    }

    public boolean canPop() {
        return mStarterCache.getBackStackCount() > 1;
    }

    public boolean popEnable() {
        return mStarterCache.getBackStackCount() > 1 && !getFragmentManager().isStateSaved();
    }

    public boolean pop() {
        if (!canPop()) {
            return false;
        }
        if (getFragmentManager().isStateSaved()) {
            return false;
        }
        // Fragment回退
        getFragmentManager().popBackStack(mStarterCache.popFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return true;
    }

    /**
     * 父级Fragment或Activity退出
     */
    public void popParent() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            if (!((AppCompatActivity) mLifecycleOwner).isFinishing()) {
                ((AppCompatActivity) mLifecycleOwner).finish();
            }
        } else {
            Fragment fragment = (Fragment) mLifecycleOwner;
            Fragment parent = fragment.getParentFragment();
            if (parent != null) {
                StarterDelegate.get(parent).pop();
            } else {
                if (!fragment.requireActivity().isFinishing()) {
                    fragment.requireActivity().finish();
                }
            }
        }
    }

    @Nullable
    public AppCompatActivity getActivity() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return (AppCompatActivity) mLifecycleOwner;
        }
        return (AppCompatActivity) ((Fragment) mLifecycleOwner).getActivity();
    }

    @NonNull
    public AppCompatActivity requireActivity() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return (AppCompatActivity) mLifecycleOwner;
        }
        return (AppCompatActivity) ((Fragment) mLifecycleOwner).requireActivity();
    }

    @NonNull
    public FragmentManager getFragmentManager() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return ((AppCompatActivity) mLifecycleOwner).getSupportFragmentManager();
        }
        return ((Fragment) mLifecycleOwner).getParentFragmentManager();
    }

    /**
     * 回退操作
     */
    public void onBackPressed() {
        if (mLifecycleOwner instanceof FragmentActivity) {
            activityPop((FragmentActivity) mLifecycleOwner);
        } else {
            fragmentPop((Fragment) mLifecycleOwner);
        }
    }

    /**
     * 指定Activity回退
     * @param activity 指定的Activity
     */
    private void activityPop(@NonNull FragmentActivity activity) {
        // 获取委托
        StarterDelegate delegate = StarterDelegate.get(activity);
        // 尝试回退Fragment
        if (delegate.popEnable()) {
            Fragment fragment = mStarterCache.getTopFragment(this);
            // 回退未成功，实现了BackPressedProvider
            if (fragment instanceof StarterProvider) {
                if (!((StarterProvider) fragment).onBackPressedSupport()) {
                    pop();
                }
            } else {
                pop();
            }
        } else {
            if (activity instanceof StarterProvider) {
                if (!((StarterProvider) activity).onBackPressedSupport()) {
                    ActivityUtils.finish(activity);
                }
            } else {
                ActivityUtils.finish(activity);
            }
        }
    }

    /**
     * 指定Fragment回退
     * @param fragment 指定的Fragment
     */
    private void fragmentPop(@NonNull Fragment fragment) {
        // 获取委托
        StarterDelegate delegate = StarterDelegate.get(fragment);
        if (delegate.popEnable()) {
            Fragment top = mStarterCache.getTopFragment(this);
            if (top instanceof StarterProvider) {
                if (!((StarterProvider) top).onBackPressedSupport()) {
                    delegate.pop();
                }
            } else {
                delegate.pop();
            }
        } else {
            Fragment parent = fragment.getParentFragment();
            if (parent == null) {
                activityPop(fragment.requireActivity());
            } else {
                fragmentPop(parent);
            }
        }
    }

    /**
     * 获取Fragment、Activity或NavIntent对应的FragmentAnimator
     * @param owner Fragment或Activity
     * @param intent NavIntent参数
     */
    @NonNull
    private FragmentAnimator getFragmentAnimator(@NonNull LifecycleOwner owner, NavIntent intent) {
        // 使用Intent设置的Fragment的动画
        if (intent != null && intent.getFragmentAnimator() != null) {
            return intent.getFragmentAnimator();
        }
        // 如果Owner实现了FragmentAnimatorProvider
        if (owner instanceof StarterProvider) {
            FragmentAnimator fragmentAnimator = ((StarterProvider) owner).onCreateFragmentAnimator();
            if (fragmentAnimator != null) {
                return fragmentAnimator;
            }
        }
        return getParentFragmentAnimator(owner);
    }

    /**
     * 获取Fragment或Activity对应的FragmentAnimator
     * @param owner Fragment或Activity
     */
    @NonNull
    private FragmentAnimator getParentFragmentAnimator(@NonNull LifecycleOwner owner) {
        if (owner instanceof Fragment) {
            Fragment parent = ((Fragment) owner).getParentFragment();
            if (parent == null) {
                return getFragmentAnimator(((Fragment) owner).requireActivity(), null);
            } else {
                return getFragmentAnimator(parent, null);
            }
        }
        return Starter.getInstance().getFragmentAnimator();
    }
}
