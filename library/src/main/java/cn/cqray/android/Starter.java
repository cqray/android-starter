package cn.cqray.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

import cn.cqray.android.app.SupportDispatcher;
import cn.cqray.java.SparseArrayTypeAdapter;
import cn.cqray.java.SparseBooleanArrayTypeAdapter;
import cn.cqray.java.SparseIntArrayTypeAdapter;
import cn.cqray.java.SparseLongArrayTypeAdapter;
import cn.cqray.java.type.ExtraTypeManager;
import lombok.experimental.Accessors;

/**
 * Android库入口
 * @author Cqray
 */
@Accessors(prefix = "m")
public class Starter {

    private static final class Holder {
        static final Starter INSTANCE = new Starter();
    }

    /** 反射所得Application **/
    private Application mApplication;
    private StarterStrategy mStarterStrategy;

    private Starter() {}

    public static Starter getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化AndroidLibrary
     * @param application Application
     */
    public void initialize(Application application) {
        initialize(application, StarterStrategy.builder().build());
    }

    /**
     * 初始化AndroidLibrary
     * @param application Application
     * @param strategy 配置策略
     */
    public void initialize(Application application, StarterStrategy strategy) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int count = elements.length;
        // 遍历方法堆栈，确保初始化进行在Application中
        for (int i = 0; i < count; i++) {
            StackTraceElement element = elements[i];
            // 找到当前类的StackTraceElement
            if (getClass().getName().equals(element.getClassName())) {
                if (i + 1 < count) {
                    StackTraceElement parentElement = elements[i + 1];
                    try {
                        Class<?> clazz = Class.forName(parentElement.getClassName());
                        // 初始化进行在Application中，则进行初始化操作
                        if (Application.class.isAssignableFrom(clazz)) {
                            mApplication = application;
                            mStarterStrategy = strategy;
                            initJavaUtils();
                            Utils.init(application);
                            SupportDispatcher.initialize(application);
                            return;
                        }
                    } catch (ClassNotFoundException ignored) {}
                }
            }
        }
        // 不是在Application中初始化，则抛出异常信息。
        throw new RuntimeException("You must initialize Starter in class which extends Application.");
    }

//    public Starter fragmentAnimator(FragmentAnimator animator) {
//        if (animator != null) {
//            mFragmentAnimator = animator;
//        }
//        return this;
//    }

    public Application getApplication() {
        if (mApplication == null) {
            throw new RuntimeException("You should call AndroidLibrary.getInstance().initialize() first.");
        }
        return mApplication;
    }

    public Context getContext() {
        return getApplication().getApplicationContext();
    }

    public StarterStrategy getStarterStrategy() {
        return mStarterStrategy;
    }

    public void initJavaUtils() {

        ExtraTypeManager.getInstance().addTypeAdapter(new SparseArrayTypeAdapter());
        ExtraTypeManager.getInstance().addTypeAdapter(new SparseBooleanArrayTypeAdapter());
        ExtraTypeManager.getInstance().addTypeAdapter(new SparseIntArrayTypeAdapter());
        ExtraTypeManager.getInstance().addTypeAdapter(new SparseLongArrayTypeAdapter());

        Log.e("数据", "chagndu:" + ExtraTypeManager.getInstance().getTypeAdapters().size());
    }
}
