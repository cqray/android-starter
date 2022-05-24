package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;

/**
 * @author LeiJue
 * @date 2022/5/19
 */
public class ContextUtils {

    public static Context get() {
        Activity act = ActivityUtils.getTopActivity();
        return act == null ? Utils.getApp().getApplicationContext() : act;
    }

    public static Resources getResources() {
        return get().getResources();
    }

    public static AssetManager getAssets() {
        return get().getAssets();
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(get(), resId);
    }

    @NonNull
    public static String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    public static View inflate(@LayoutRes int resId) {
        Activity act = ActivityUtils.getTopActivity();
        ViewGroup parent = null;
        if (act != null) {
            parent = act.findViewById(android.R.id.content);
        }
        return LayoutInflater.from(get()).inflate(resId, parent, false);
    }
}
