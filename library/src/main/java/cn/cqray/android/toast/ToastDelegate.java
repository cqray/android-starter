package cn.cqray.android.toast;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.cqray.android.Starter;

/**
 * Toast弹窗委托
 * @author Cqray
 */
public class ToastDelegate {

    public static final int NORMAL = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final int SUCCESS = 4;

    @IntDef({NORMAL, INFO, WARNING, ERROR, SUCCESS})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Level {}

    /** 默认时长 **/
    private int mDuration = 1500;
    /** 适配器 **/
    private volatile Adapter mAdapter;

    public ToastDelegate() {}

    public void normal(String text) {
        getAdapter().show(NORMAL, text, mDuration);
    }

    public void normal(String text, int duration) {
        getAdapter().show(NORMAL, text, duration);
    }

    public void info(String text) {
        getAdapter().show(INFO, text, mDuration);
    }

    public void info(String text, int duration) {
        getAdapter().show(INFO, text, duration);
    }

    public void warning(String text) {
        getAdapter().show(WARNING, text, mDuration);
    }

    public void warning(String text, int duration) {
        getAdapter().show(WARNING, text, duration);
    }

    public void error(String text) {
        getAdapter().show(ERROR, text, mDuration);
    }

    public void error(String text, int duration) {
        getAdapter().show(ERROR, text, duration);
    }

    public void success(String text) {
        getAdapter().show(SUCCESS, text, mDuration);
    }

    public void success(String text, int duration) {
        getAdapter().show(SUCCESS, text, duration);
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    protected Adapter getAdapter() {
        if (mAdapter == null) {
            synchronized (ToastDelegate.class) {
                if (mAdapter == null) {
                    mAdapter = (Adapter) (level, text, duration) -> {
                        Context context = Starter.getInstance().getContext();
                        Toast.makeText(context, text, duration).show();
                    };
                }
            }
        }
        return mAdapter;
    }

    public interface Adapter extends Serializable {
        /**
         * 显示Toast
         * @param level    Toast级别
         * @param text     文本内容
         * @param duration 显示时间
         */
        void show(@Level int level, String text, int duration);
    }
}
