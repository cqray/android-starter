package cn.cqray.android.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

/**
 * @author Cqray
 */
public class SupportDelegate {

    private LifecycleOwner mLifecycleOwner;
    private final ViewDelegate mViewDelegate;

    public SupportDelegate(AppCompatActivity activity) {
        mLifecycleOwner = activity;
        mViewDelegate = new ViewDelegate(activity);
    }

    public SupportDelegate(Fragment fragment) {
        mLifecycleOwner = fragment;
        mViewDelegate = new ViewDelegate(fragment);
    }

    /**
     * @see ApplicationLifecycleDispatcher#onActivityCreated(Activity, Bundle)
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentCreated(FragmentManager, Fragment, Bundle)
     * @param savedInstanceState 保存的参数
     */
    protected void onCreated(@Nullable Bundle savedInstanceState) {}

    /**
     * @see ApplicationLifecycleDispatcher#onActivityStarted(Activity)
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentResumed(FragmentManager, Fragment)
     */
    protected void onResumed() {}

    /**
     * @see ApplicationLifecycleDispatcher#onActivityStarted(Activity)
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentStarted(FragmentManager, Fragment)
     */
    protected void onStarted() {}

    /**
     * @see ApplicationLifecycleDispatcher#onActivityPaused(Activity)
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentPaused(FragmentManager, Fragment)
     */
    protected void onPaused() {}

    /**
     * @see ApplicationLifecycleDispatcher#onActivityStopped(Activity)
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentStopped(FragmentManager, Fragment)
     */
    protected void onStopped() {}

    /**
     * @see ApplicationLifecycleDispatcher#onActivitySaveInstanceState(Activity, Bundle)
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentSaveInstanceState(FragmentManager, Fragment, Bundle)
     */
    protected void onSaveInstanceState(@NonNull Bundle outState) {}

    /**
     * @see ApplicationLifecycleDispatcher#onActivityDestroyed(Activity) 
     * @see ApplicationLifecycleDispatcher.SupportFragmentCallbacks#onFragmentDestroyed(FragmentManager, Fragment)
     */
    protected void onDestroyed() {}

    public void setContentView(@LayoutRes int id) {
        mViewDelegate.setContentView(id);
    }

    public void setContentView(View view) {
        mViewDelegate.setContentView(view);
    }

    public void setNativeContentView(@LayoutRes int id) {
        mViewDelegate.setNativeContentView(id);
    }

    public void setNativeContentView(View view) {
        mViewDelegate.setNativeContentView(view);
    }
}
