package cn.cqray.android.app2;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Get实例
 * @author Cqray
 */
public class Get {

//    private static volatile Get mInstance;
//
//    private final cn.cqray.android.get.GetManager mGetManager = new cn.cqray.android.get.GetManager();
//
//    private Get() {}
//
//    private static Get getInstance() {
//        if (mInstance == null) {
//            synchronized (Get.class) {
//                if (mInstance == null) {
//                    mInstance = new Get();
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    public static void init(Application application) {
//        getInstance().mGetManager.setApplication(application);
//    }
//
//    @NonNull
//    public static Application getApplication() {
//        Application application = getInstance().mGetManager.getApplication();
//        if (application == null) {
//            throw new IllegalStateException("Please call Get.init first.");
//        }
//        return application;
//    }
//
//    @NonNull
//    public static Context getContext() {
//        return getApplication().getApplicationContext();
//    }
}
