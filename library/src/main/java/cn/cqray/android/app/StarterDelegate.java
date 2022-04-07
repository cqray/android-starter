package cn.cqray.android.app;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Map;

import cn.cqray.android.lifecycle.LifecycleViewModelProvider;

/**
 * Fragment启动委托
 * @author Cqray
 */
public final class StarterDelegate {

    /** 委托缓存集合 **/
    private static final Map<LifecycleOwner, StarterDelegate> DELEGATE_MAP = new HashMap<>();

    private StarterViewModel mMainViewModel;
    private final LifecycleOwner mLifecycleOwner;

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
        mMainViewModel = new LifecycleViewModelProvider(requireActivity()).get(StarterViewModel.class);
        // Fragment还需做回退处理
        if (mLifecycleOwner instanceof Fragment) {
            Fragment fragment = ((Fragment) mLifecycleOwner);
            FragmentActivity activity = fragment.requireActivity();
            OnBackPressedDispatcher dispatcher = activity.getOnBackPressedDispatcher();
            dispatcher.addCallback(fragment, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    mMainViewModel.onBackPressed();
                }
            });
        }
    }

    void onDestroyed() {
        DELEGATE_MAP.remove(mLifecycleOwner);
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent NavIntent
     */
    public void loadRootFragment(@IdRes int containerId, NavIntent intent) {
        mMainViewModel.loadRootFragment(containerId, intent);
    }

    /**
     * 启动Fragment
     * @param intent NavIntent
     */
    public void start(@NonNull NavIntent intent) {
        mMainViewModel.start(intent);
    }

    /**
     * 回退到指定的Fragment
     * @param popTo 指定的Fragment
     * @param inclusive 是否包含指定的Fragment
     */
    public void popTo(Class<?> popTo, boolean inclusive) {
        mMainViewModel.popTo(popTo, inclusive);
    }

    public boolean canPop() {
        //return mStarterCache.getBackStackCount() > 1;
        return false;
    }

    public void pop() {
        mMainViewModel.pop();
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
}
