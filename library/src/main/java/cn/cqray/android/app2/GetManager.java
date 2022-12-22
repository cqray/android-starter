package cn.cqray.android.app2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


//import com.blankj.utilcode.util.Utils;
//import com.blankj.utilcode.util.UtilsBridge;

/**
 * 生命周期管理器
 * @author Cqray
 */
public class GetManager implements Application.ActivityLifecycleCallbacks {

    /** 所有Activity数量 **/
    private int mActivityCount;
    /** 处于前台的Activity数量 **/
    private int mForegroundCount;
    /** 配置Configuration的Activity数量 **/
    private int mConfigurationCount;
    /** 是否处于后台 **/
    private boolean mIsBackground;
    /** Application实例 **/
    private Application mApplication;

    /** 生命周期监听 **/
    private final Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {


        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };

    public void setApplication(Application application) {
        if (mApplication == null) {
            synchronized (GetManager.class) {
                mApplication = application;
                mApplication.registerActivityLifecycleCallbacks(mCallbacks);
            }
        }
        //AppUtils.registerAppStatusChangedListener();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (mActivityCount == 0) {
            onUiCreated();
        }
        mActivityCount++;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!mIsBackground) {
            //setTopActivity(activity);
        }
        if (mConfigurationCount < 0) {
            ++mConfigurationCount;
        } else {
            ++mForegroundCount;
        }
        //consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_START);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
//        if (mActivityCount > 0) {
//
//        }
//        setTopActivity(activity);
        if (mIsBackground) {
            mIsBackground = false;
            postStatus(activity, true);
        }
        processHideSoftInputOnActivityDestroy(activity, false);
//        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (activity.isChangingConfigurations()) {
            --mConfigurationCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                mIsBackground = true;
                postStatus(activity, false);
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (mActivityCount == 1) {
            onUiDestroyed();
        }
        mActivityCount--;
//        mActivityList.remove(activity);
 //       KeyboardUtils.fixSoftInputLeaks(activity);
//        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY);
    }


    protected void onGetCreate() {

    }

    protected void onGetTerminate() {

    }

    public void onUiCreated() {
        Log.e("数据", "创建UI");
    }

    public void onUiDestroyed() {
        Log.e("数据", "销毁UI");
    }

    public Application getApplication() {
        return mApplication;
    }

    public boolean isAppForeground() {
        return mForegroundCount <= 0;
    }

    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    private void processHideSoftInputOnActivityDestroy(final Activity activity, boolean isSave) {
        try {
            if (isSave) {
                Window window = activity.getWindow();
                final WindowManager.LayoutParams attrs = window.getAttributes();
                final int softInputMode = attrs.softInputMode;
                window.getDecorView().setTag(-123, softInputMode);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } else {
                final Object tag = activity.getWindow().getDecorView().getTag(-123);
                if (!(tag instanceof Integer)) {
                    return;
                }
//                UtilsBridge.runOnUiThreadDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Window window = activity.getWindow();
//                            if (window != null) {
//                                window.setSoftInputMode(((Integer) tag));
//                            }
//                        } catch (Exception ignore) {
//                        }
//                    }
//                }, 100);
            }
        } catch (Exception ignore) {
        }
    }

    private void postStatus(final Activity activity, final boolean isForeground) {
//        if (mStatusListeners.isEmpty()) return;
//        for (Utils.OnAppStatusChangedListener statusListener : mStatusListeners) {
//            if (isForeground) {
//                statusListener.onForeground(activity);
//            } else {
//                statusListener.onBackground(activity);
//            }
//        }
    }
}
