package cn.cqray.android.app;

import android.app.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;

import java.util.HashMap;
import java.util.Map;

import cn.cqray.android.exception.ExceptionDispatcher;
import cn.cqray.android.exception.ExceptionType;
import cn.cqray.android.lifecycle.LifecycleViewModelProvider;

/**
 * Fragment启动委托
 * @author Cqray
 */
public final class SupportDelegate {

    /** 委托缓存 **/
    private static final Map<Object, SupportDelegate> DELEGATE_CACHE = new HashMap<>();

    @NonNull
    public static SupportDelegate get(@NonNull SupportProvider provider) {
        SupportDelegate delegate = DELEGATE_CACHE.get(provider);
        if (delegate == null) {
            delegate = new SupportDelegate(provider);
        }
        return delegate;
    }

    private SupportProvider mProvider;
    private SupportViewModel mMainViewModel;

    private SupportDelegate(@NonNull SupportProvider provider) {
        mProvider = provider;
        SupportUtils.checkProvider(provider);
        DELEGATE_CACHE.put(provider, this);
    }

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

        // 是否自动隐藏键盘
        if (mProvider.onKeyboardAutoHide()) {
            Activity act = ActivityUtils.getTopActivity();
            KeyboardUtils.hideSoftInput(act);
        }
    }

    void onDestroyed() {
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
            mMainViewModel.popTo(popTo, inclusive);
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
