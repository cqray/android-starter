package cn.cqray.android.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 应用调度器
 * 用来统一调度AppCompatActivity和Fragment的生命周期
 * @author Cqray
 */
public class SupportDispatcher {

    /** 调度器单例 **/
    private static volatile SupportDispatcher sInstance;

    /**
     * 初始化调度器
     * @param application Application
     */
    public static void initialize(Application application) {
        if (sInstance == null) {
            synchronized (SupportDispatcher.class) {
                if (sInstance == null) {
                    sInstance = new SupportDispatcher(application);
                }
            }
        }
    }

    private SupportDispatcher(@NonNull Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if (activity instanceof SupportProvider) {
                    activity.getIntent().putExtra("activity:isTranslucentOrFloating", isTranslucentOrFloating(activity));
                    SupportDelegate.get((SupportProvider) activity).onCreated();
                    new SupportFragmentCallbacks((AppCompatActivity) activity);
                }
                hookOrientation(activity);
            }

            @Override
            public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if (activity instanceof SupportProvider) {
                    SupportDelegate.get((SupportProvider) activity).onViewCreated();
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {}

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (activity instanceof SupportProvider) {
                    SupportDelegate.get((SupportProvider) activity).onDestroyed();
                }
            }
        });
    }

    /**
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     * 修复android 8.0存在的问题
     * 在Activity中onCreate()中super之前调用
     */
    private static void hookOrientation(@NonNull Activity activity) {
        // 目标版本8.0及其以上
        if (activity.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O) {
            if (isTranslucentOrFloating(activity)) {
                fixOrientation(activity);
            }
        }
    }

    /**
     * 设置屏幕不固定，绕过检查
     */
    private static void fixOrientation(@NonNull Activity activity) {
        try {
            Class<Activity> activityClass = Activity.class;
            Field mActivityInfoField = activityClass.getDeclaredField("mActivityInfo");
            mActivityInfoField.setAccessible(true);
            ActivityInfo activityInfo = (ActivityInfo) mActivityInfoField.get(activity);
            // 设置屏幕不固定
            assert activityInfo != null;
            activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        } catch (Exception ignored) {}
    }

    /**
     * 检查屏幕横竖屏或者锁定就是固定
     */
    private static boolean isTranslucentOrFloating(Activity activity) {
        boolean isTranslucentOrFloating = false;
        try {
            @SuppressLint("PrivateApi")
            Class<?> styleableClass = Class.forName("com.android.internal.R$styleable");
            Field windowField = styleableClass.getDeclaredField("Window");
            windowField.setAccessible(true);
            int[] styleableRes = (int[]) windowField.get(null);
            // 先获取到TypedArray
            assert styleableRes != null;
            final TypedArray typedArray = activity.obtainStyledAttributes(styleableRes);
            Class<?> activityInfoClass = ActivityInfo.class;
            // 调用检查是否屏幕旋转
            @SuppressLint("DiscouragedPrivateApi")
            Method isTranslucentOrFloatingMethod = activityInfoClass.getDeclaredMethod("isTranslucentOrFloating", TypedArray.class);
            isTranslucentOrFloatingMethod.setAccessible(true);
            isTranslucentOrFloating = (boolean) isTranslucentOrFloatingMethod.invoke(null, typedArray);
        } catch (Exception ignored) {}
        return isTranslucentOrFloating;
    }

    /**
     * Fragment生命周期回调
     */
    static final class SupportFragmentCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        public SupportFragmentCallbacks(@NonNull AppCompatActivity activity) {
            activity.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // 注销回调监听
                    activity.getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(this);
                }
            });
            // 注册回调监听
            activity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(this, true);
        }

        @Override
        public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            if (f instanceof SupportProvider) {
                SupportDelegate.get((SupportProvider) f).onCreated();
            }
        }

        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
            if (f instanceof SupportProvider) {
                SupportDelegate.get((SupportProvider) f).onViewCreated();
            }
        }

        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
            super.onFragmentDestroyed(fm, f);
            if (f instanceof SupportProvider) {
                SupportDelegate.get((SupportProvider) f).onDestroyed();
            }
        }
    }
}
