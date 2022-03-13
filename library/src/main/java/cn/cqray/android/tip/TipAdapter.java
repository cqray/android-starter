package cn.cqray.android.tip;

import androidx.annotation.Nullable;

/**
 * 提示适配器
 * @author Cqray
 * @date 2022/3/12
 */
public interface TipAdapter {

    /**
     * 显示Toast
     * @param tag       消息标识
     * @param level     Toast级别
     * @param text      文本内容
     * @param duration  显示时间
     */
    void show(@Nullable Object tag, TipLevel level, String text, int duration);
}
