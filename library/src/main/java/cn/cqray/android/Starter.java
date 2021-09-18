package cn.cqray.android;

import android.app.Application;
import android.content.Context;

import cn.cqray.android.anim.DefaultVerticalAnimator;
import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.app.ApplicationLifecycleDispatcher;

/**
 * Android库入口
 * @author Cqray
 */
public class Starter {

    private static final class Holder {
        static final Starter INSTANCE = new Starter();
    }

    /** 反射所得Application **/
    private Application mApplication;
    /** Fragment切换动画 **/
    private FragmentAnimator mFragmentAnimator;

    private Starter() {
        mFragmentAnimator = new DefaultVerticalAnimator();
    }

    public static Starter getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化AndroidLibrary
     * @param application Application
     */
    public Starter initialize(Application application) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int count = elements.length;
        for (int i = 0; i < count; i++) {
            StackTraceElement element = elements[i];
            if (getClass().getName().equals(element.getClassName())) {
                if (i + 1 < count) {
                    StackTraceElement parentElement = elements[i + 1];
                    try {
                        Class<?> clazz = Class.forName(parentElement.getClassName());
                        if (Application.class.isAssignableFrom(clazz)) {
                            mApplication = application;
                            //ActivityHelper.initialize(application);
                            ApplicationLifecycleDispatcher.initialize(application);
                            return this;
                        }
                    } catch (ClassNotFoundException ignored) {}
                }
            }
        }
        throw new RuntimeException("You must initialize AndroidLibrary in class which extends Application.");
    }

    public Starter fragmentAnimator(FragmentAnimator animator) {
        if (animator != null) {
            mFragmentAnimator = animator;
        }
        return this;
    }


    public Application getApplication() {
        if (mApplication == null) {
            throw new RuntimeException("You should call AndroidLibrary.getInstance().initialize() first.");
        }
        return mApplication;
    }

    public Context getContext() {
        return getApplication().getApplicationContext();
    }

    public FragmentAnimator getFragmentAnimator() {
        return mFragmentAnimator;
    }
}
