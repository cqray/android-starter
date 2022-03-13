package cn.cqray.android.util;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;

import java.util.Stack;

/**
 * Activity工具类
 * @author Cqray
 */
public final class ActivityUtils {

    private static class Holder {
        private static final ActivityUtils INSTANCE = new ActivityUtils();
    }

    /** 是否注册 **/
    private boolean mRegistered;
    /** 前台活动数量 **/
    private int mAliveCount;
    /** Activity堆栈 **/
    private final Stack<Activity> mActivityStack;
    /** 是否在后台状态 **/
    private final MutableLiveData<Boolean> mBackgroundState;

    private ActivityUtils() {
        mActivityStack = new Stack<>();
        mBackgroundState = new MutableLiveData<>();
    }

    /**
     * 通过Application初始化
     * @param application Application
     */
    public static void initialize(Application application) {
        // 防止重复初始化
        synchronized (ActivityUtils.class) {
            if (Holder.INSTANCE.mRegistered) {
                return;
            }
            Holder.INSTANCE.mRegistered = true;
        }
        // 注册监听事件
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                Holder.INSTANCE.mActivityStack.add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (Holder.INSTANCE.mAliveCount == 0) {
                    Holder.INSTANCE.mBackgroundState.postValue(false);
                }
                Holder.INSTANCE.mAliveCount ++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                if (Holder.INSTANCE.mAliveCount == 1) {
                    Holder.INSTANCE.mBackgroundState.postValue(true);
                }
                Holder.INSTANCE.mAliveCount --;
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Holder.INSTANCE.mActivityStack.remove(activity);
            }
        });
    }

    public static MutableLiveData<Boolean> getBackgroundState() {
        return Holder.INSTANCE.mBackgroundState;
    }

    /**
     * 指定Activity出栈
     * @param activity 指定Activity
     */
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
     * 指定Activity出栈
     * @param clazz 指定Activity类
     */
    public static void finish(@NonNull Class<? extends Activity> clazz) {
        Stack<Activity> stack = Holder.INSTANCE.mActivityStack;
        int count = stack.size();
        for (int i = count - 1; i >= 0; i--) {
            Activity act = stack.get(i);
            if (clazz == act.getClass()) {
                finish(act);
                break;
            }
        }
    }

    /**
     * 回退Activity
     */
    public static void pop() {
        Stack<Activity> stack = Holder.INSTANCE.mActivityStack;
        if (stack.size() != 0) {
            finish(stack.peek());
        }
    }

    /**
     * 除了指定Activity所有Activity出栈
     * @param activity 指定Activity
     */
    public static void popAllExclusive(@NonNull Activity activity) {
        for (Activity act : Holder.INSTANCE.mActivityStack) {
            if (act == activity) {
                continue;
            }
            finish(activity);
        }
    }

    /**
     * 除了指定Activity所有Activity出栈
     * @param clazz 指定Activity
     */
    public static void popAllExclusive(Class<? extends Activity> clazz) {
        for (Activity act : Holder.INSTANCE.mActivityStack) {
            if (act.getClass() == clazz) {
                continue;
            }
            finish(act);
        }
    }

    /**
     * 所有Activity出栈
     */
    public static void popAll() {
        for (Activity act : Holder.INSTANCE.mActivityStack) {
            finish(act);
        }
    }

    /**
     * 退出Activity直到指定Activity
     * @param clazz 指定Activity
     * @param inclusive 退出是否包含指定Activity
     */
    public static void popTo(Class<? extends Activity> clazz, boolean inclusive) {
        Stack<Activity> stack = Holder.INSTANCE.mActivityStack;
        int count = stack.size();
        for (int i = count - 1; i >= 0; i--) {
            Activity act = stack.get(i);
            if (act.getClass() != clazz) {
                finish(act);
            } else {
                if (inclusive) {
                    finish(act);
                }
                break;
            }
        }
    }

    /**
     * 获取当前Activity，可为null
     */
    @Nullable
    public static Activity getCurrent() {
        Activity activity = null;
        Stack<Activity> stack = Holder.INSTANCE.mActivityStack;
        int count = stack.size();
        for (int i = count - 1; i >= 0; i--) {
            Activity act = stack.get(i);
            if (act.isFinishing()) {
                continue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (act.isDestroyed()) {
                    continue;
                }
            } else if (act instanceof AppCompatActivity) {
                Lifecycle.State state = ((AppCompatActivity) act).getLifecycle().getCurrentState();
                if (state == Lifecycle.State.DESTROYED) {
                    continue;
                }
            }
            activity = act;
            break;
        }
        return activity;
    }

    /**
     * 获取当前Activity，为null会抛出异常
     */
    @NonNull
    public static Activity requireCurrent() {
        Activity act = getCurrent();
        if (act == null) {
            throw new IllegalStateException("Please make sure that there is alive activity there.");
        }
        return act;
    }

    /**
     * 获取Activity堆栈
     */
    public static Stack<Activity> getActivityStack() {
        return Holder.INSTANCE.mActivityStack;
    }

    /**
     * 是否包含指定Activity
     * @param clazz 指定Activity
     */
    public static boolean contains(@NonNull Class<? extends Activity> clazz) {
        for (Activity act : Holder.INSTANCE.mActivityStack) {
            if (clazz.getName().equals(act.getClass().getName())) {
                return true;
            }
        }
        return false;
    }

}
