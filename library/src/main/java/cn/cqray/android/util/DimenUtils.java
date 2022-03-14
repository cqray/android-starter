package cn.cqray.android.util;

import android.content.res.Resources;

import androidx.annotation.DimenRes;

import cn.cqray.android.Starter;

/**
 * 尺寸管理
 * @author Cqray
 */
public class DimenUtils {

    private DimenUtils() {
        throw new UnsupportedOperationException("can't instantiate me...");
    }

    public static int toPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static float toDp(float px) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return px / density;
    }

    public static int get(@DimenRes int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    public static float getDp(@DimenRes int resId) {
        return toDp(getResources().getDimensionPixelSize(resId));
    }

    private static Resources getResources() {
        return Starter.getInstance().getContext().getResources();
    }
}
