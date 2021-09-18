package cn.cqray.android.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;

/**
 * Activity管理辅助类
 * @author Cqray
 */
public final class ActivityHelper {

    private static volatile ActivityHelper mInstance;
    /** 前台活动数量 **/
    private int mAliveCount;
    /** Activity堆栈 **/
    private final Stack<Activity> mActivityStack = new Stack<>();
    /** 应用状态 **/
    private final MutableLiveData<ApplicationState> mApplicationState = new MutableLiveData<>();

    private ActivityHelper() {}

    private static ActivityHelper get() {
        if (mInstance == null) {
            synchronized (ActivityHelper.class) {
                if (mInstance == null) {
                    mInstance = new ActivityHelper();
                }
            }
        }
        return mInstance;
    }

    public static void observeApplicatonState(LifecycleOwner owner, Observer<ApplicationState> observer) {
        get().mApplicationState.observe(owner, observer);
    }

    /**
     * 通过Application初始化
     * @param application Application
     */
    public static void initialize(Application application) {
        // 防止重复初始化
        synchronized (get().mApplicationState) {
            if (get().mApplicationState.getValue() != null) {
                return;
            }
        }
        // 设置状态为初始化
        get().mApplicationState.postValue(ApplicationState.INITIALIZE);
        // 注册监听事件
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                get().mActivityStack.add(activity);
                Log.e("数据", "onActivityCreated");
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (get().mAliveCount == 0) {
                    get().mApplicationState.postValue(ApplicationState.FOREGROUND);
                }
                get().mAliveCount ++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                if (get().mAliveCount == 1) {
                    get().mApplicationState.postValue(ApplicationState.BACKGROUND);
                }
                get().mAliveCount --;
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                get().mActivityStack.remove(activity);
                if (get().mActivityStack.size() == 0) {
                    get().mApplicationState.postValue(ApplicationState.EXITED);
                }
            }
        });
    }

    /**
     * 指定Activity出栈
     * @param activity 指定Activity
     */
    public static void pop(@NonNull Activity activity) {
        Stack<Activity> activities = get().mActivityStack;
        for (Activity act : activities) {
            if (act == activity && !activity.isFinishing()) {
                activity.finish();
                activities.remove(activity);
                break;
            }
        }
    }

    /**
     * 指定Activity出栈
     * @param clazz 指定Activity
     */
    public static void pop(@NonNull Class<? extends Activity> clazz) {
        Stack<Activity> activities = get().mActivityStack;
        for (Activity act : activities) {
            if (clazz == act.getClass() && !act.isFinishing()) {
                act.finish();
                activities.remove(act);
                break;
            }
        }
    }

    /**
     * 除了指定Activity所有Activity出栈
     * @param activity 指定Activity
     */
    public static void popAllExclusive(@NonNull Activity activity) {
        Stack<Activity> activities = get().mActivityStack;
        for (Activity act : activities) {
            if (act == activity) {
                continue;
            }
            if (!act.isFinishing()) {
                act.finish();
            }
        }
        activities.clear();
        activities.add(activity);
    }

    /**
     * 除了指定Activity所有Activity出栈
     * @param clazz 指定Activity
     */
    public static void popAllExclusive(Class<? extends Activity> clazz) {
        Stack<Activity> activities = get().mActivityStack;
        Activity activity = null;
        for (Activity act : activities) {
            if (act.getClass() == clazz) {
                activity = act;
                continue;
            }
            if (!act.isFinishing()) {
                act.finish();
            }
        }
        activities.clear();
        if (activity != null) {
            activities.add(activity);
        }
    }

    /**
     * 所有Activity出栈
     */
    public static void popAll() {
        Stack<Activity> activities = get().mActivityStack;
        for (Activity act : activities) {
            if (!act.isFinishing()) {
                act.finish();
            }
        }
        activities.clear();
    }

    /**
     * 退出Activity直到指定Activity
     * @param clazz 指定Activity
     * @param inclusive 退出是否包含指定Activity
     */
    public static void popTo(Class<? extends Activity> clazz, boolean inclusive) {
        Stack<Activity> activities = get().mActivityStack;
        int count = activities.size();
        for (int i = count - 1; i >= 0; i--) {
            Activity act = activities.get(i);
            if (act.getClass() != clazz) {
                remove(act, i);
            } else {
                if (inclusive) {
                    remove(act, i);
                }
                break;
            }
        }
    }

    public static void finish(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isDestroyed()) {
                activity.finish();
            }
        } else if (activity instanceof AppCompatActivity) {
            AppCompatActivity act = (AppCompatActivity) activity;
            if (act.getLifecycle()
                    .getCurrentState()
                    .isAtLeast(Lifecycle.State.INITIALIZED)) {
                activity.finish();
            }
        }
    }

    /**
     * 移除指定位置的Activity
     * @param act Activity
     * @param index 索引
     */
    private static void remove(@NonNull Activity act, int index) {
        get().mActivityStack.remove(index);
        if (!act.isFinishing()) {
            act.finish();
        }
    }

    /**
     * 获取栈顶Activity，可为null
     */
    @Nullable
    public static Activity peek() {
        return count() > 0 ? get().mActivityStack.lastElement() : null;
    }

    /**
     * 获取栈顶Activity，不为null，会报异常
     */
    @NonNull
    public static Activity requirePeek() {
        return get().mActivityStack.lastElement();
    }

    /**
     * 栈内Activity数量
     */
    public static int count() {
        return get().mActivityStack.size();
    }

    /**
     * 指定Activity是否在栈顶
     * @param activity 指定Activity
     */
    public static boolean isTop(@NonNull Activity activity) {
        return peek() == activity;
    }

    /**
     * 指定Activity是否在栈顶
     * @param clazz 指定Activity
     */
    public static boolean isTop(@NonNull Class<? extends Activity> clazz) {
        Activity act = peek();
        return act != null && clazz.equals(act.getClass());
    }

    /**
     * 是否包含指定Activity
     * @param activity 指定Activity
     */
    public static boolean contains(@NonNull Activity activity) {
        return get().mActivityStack.contains(activity);
    }

    /**
     * 是否包含指定Activity
     * @param clazz 指定Activity
     */
    public static boolean contains(@NonNull Class<? extends Activity> clazz) {
        for (Activity act : get().mActivityStack) {
            if (clazz.getName().equals(act.getClass().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     * 修复android 8.0存在的问题
     * 在Activity中onCreate()中super之前调用
     */
    public static void hookOrientation(@NonNull Activity activity) {
        //目标版本8.0及其以上
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
}
