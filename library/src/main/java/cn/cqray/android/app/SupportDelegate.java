package cn.cqray.android.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.cqray.android.exception.ExceptionDispatcher;
import cn.cqray.android.exception.ExceptionType;
import cn.cqray.android.lifecycle.LifecycleViewModelProvider;

/**
 * Fragment启动管理委托
 * @author Cqray
 */
public final class SupportDelegate {

    /** 委托缓存 **/
    private static final Map<SupportProvider, SupportDelegate> DELEGATE_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取委托实例
     * @param provider 功能提供器
     */
    @NonNull
    public synchronized static SupportDelegate get(@NonNull SupportProvider provider) {
        SupportDelegate delegate = DELEGATE_CACHE.get(provider);
        if (delegate == null) {
            delegate = new SupportDelegate(provider);
        }
        return delegate;
    }

    /** 界面提供器 **/
    private SupportProvider mProvider;
    /** 相关ViewModel **/
    private SupportViewModel mMainViewModel;
    /** Handler对象 **/
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private SupportDelegate(@NonNull SupportProvider provider) {
        mProvider = provider;
        SupportUtils.checkProvider(provider);
        DELEGATE_CACHE.put(provider, this);
    }

    /**
     * 在{@link Application.ActivityLifecycleCallbacks#onActivityCreated(Activity, Bundle)}中被调用
     * 在{@link FragmentManager.FragmentLifecycleCallbacks#onFragmentCreated(FragmentManager, Fragment, Bundle)}中被调用
     */
    void onCreated() {
        // Fragment还需做回退处理
        if (mProvider instanceof Fragment) {
            Fragment fragment = (Fragment) mProvider;
            FragmentActivity activity = fragment.requireActivity();
            OnBackPressedDispatcher dispatcher = activity.getOnBackPressedDispatcher();
            dispatcher.addCallback(fragment, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    mMainViewModel.onBackPressed();
                }
            });
            mMainViewModel = new LifecycleViewModelProvider(activity).get(SupportViewModel.class);
        } else {
            mMainViewModel = new LifecycleViewModelProvider((AppCompatActivity) mProvider).get(SupportViewModel.class);
        }
    }

    /**
     * 在{@link Application.ActivityLifecycleCallbacks#onActivityPostCreated(Activity, Bundle)}中被调用
     * 在{@link FragmentManager.FragmentLifecycleCallbacks#onFragmentViewCreated(FragmentManager, Fragment, View, Bundle)}中被调用
     */
    void onViewCreated() {
        int enterAnimDuration;
        if (mProvider instanceof Fragment) {
            // 获取Fragment进入时的动画时长
            enterAnimDuration = mMainViewModel.getAnimDuration();
        } else {
            // 获取Activity进入时的动画时长
            int animResId = SupportUtils.getActivityOpenEnterAnimationResId((Activity) mProvider);
            enterAnimDuration = SupportUtils.getAnimDurationFromResource(animResId);
        }
        // 进入动画结束回调
        mHandler.postDelayed(()-> mProvider.onEnterAnimEnd(), enterAnimDuration);
    }

    /**
     * 在{@link Application.ActivityLifecycleCallbacks#onActivityDestroyed(Activity)}中被调用
     * 在{@link FragmentManager.FragmentLifecycleCallbacks#onFragmentDestroyed(FragmentManager, Fragment)}中被调用
     */
    void onDestroyed() {
        // 移除所有事件
        mHandler.removeCallbacksAndMessages(null);
        // 移除缓存
        DELEGATE_CACHE.remove(mProvider);
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent NavIntent
     */
    public void loadRootFragment(@IdRes int containerId, NavIntent intent) {
        if (isViewModelReady()) {
            mMainViewModel.loadRootFragment(containerId, intent);
        }
    }

    /**
     * 启动Fragment
     * @param intent NavIntent
     */
    public void start(@NonNull NavIntent intent) {
        if (isViewModelReady()) {
            mMainViewModel.start(intent);
        }
    }

    /**
     * 回退到指定的Fragment
     * @param popTo 指定的Fragment
     * @param inclusive 是否包含指定的Fragment
     */
    public void popTo(Class<?> popTo, boolean inclusive) {
        if (isViewModelReady()) {
            mMainViewModel.popTo(popTo, null, inclusive);
        }
    }

    public boolean canPop() {
        //return mStarterCache.getBackStackCount() > 1;
        return false;
    }

    /**
     * 界面回退
     */
    public void pop() {
        if (isViewModelReady()) {
            mMainViewModel.pop();
        }
    }

    public View getContainerView() {
        return mMainViewModel.getContainerView();
    }

    /**
     * ViewModel是否准备完毕
     */
    private boolean isViewModelReady() {
        if (mMainViewModel == null) {
            ExceptionDispatcher.dispatchThrowable(mProvider, ExceptionType.STARTER_ILLEGAL_STATE);
            return false;
        }
        return true;
    }
}
