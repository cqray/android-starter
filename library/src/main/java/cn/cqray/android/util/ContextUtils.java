package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

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
}
