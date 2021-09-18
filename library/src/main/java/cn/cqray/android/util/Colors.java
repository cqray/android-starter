package cn.cqray.android.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;
import cn.cqray.android.Starter;

/**
 * 颜色管理
 * @author Cqray
 */
public class Colors {

    private Colors() {
        throw new UnsupportedOperationException("can't instantiate me...");
    }

    public static int foreground() {
        return get(R.color.foreground);
    }

    public static int background() {
        return get(R.color.background);
    }

    public static int primary() {
        return get(R.color.colorPrimary);
    }

    public static int primaryDark() {
        return get(R.color.colorPrimaryDark);
    }

    public static int accent() {
        return get(R.color.colorAccent);
    }

    public static int text() {
        return get(R.color.text);
    }

    public static int hint() {
        return get(R.color.hint);
    }

    public static int tint() {
        return get(R.color.tint);
    }

    public static int divider() {
        return get(R.color.divider);
    }

    public static int disable() {
        return get(R.color.disable);
    }

    public static int get(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    public static ColorStateList getStateList(@ColorRes int resId) {
        return ContextCompat.getColorStateList(getContext(), resId);
    }

    public static int parse(String color) {
        return Color.parseColor(color);
    }

    private static Context getContext() {
        return Starter.getInstance().getContext();
    }
}
