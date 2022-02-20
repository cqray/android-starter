package cn.cqray.android.toast;

import java.io.Serializable;

public interface ToastAdapter extends Serializable {
    /**
     * 显示Toast
     * @param level    Toast级别
     * @param text     文本内容
     * @param duration 显示时间
     */
    void show(@ToastDelegate.Level int level, String text, int duration);

}