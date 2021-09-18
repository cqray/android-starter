package cn.cqray.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

import androidx.annotation.DimenRes;

import cn.cqray.android.R;
import cn.cqray.android.Starter;

/**
 * 尺寸管理
 * @author Cqray
 */
public class Dimens {

    private Dimens() {
        throw new UnsupportedOperationException("can't instantiate me...");
    }
    
    public static int h1() {
        return get(R.dimen.h1);
    }

    public static int h2() {
        return get(R.dimen.h2);
    }

    public static int h3() {
        return get(R.dimen.h3);
    }

    public static int body() {
        return get(R.dimen.body);
    }

    public static int caption() {
        return get(R.dimen.caption);
    }

    public static int line() {
        return get(R.dimen.line);
    }

    public static int content() {
        return get(R.dimen.content);
    }

    public static int small() {
        return get(R.dimen.small);
    }

    public static float dpCaption() {
        return getDp(R.dimen.caption);
    }

    public static float dpBody() {
        return getDp(R.dimen.body);
    }

    public static float dpH1() {
        return getDp(R.dimen.h1);
    }

    public static float dpH2() {
        return getDp(R.dimen.h2);
    }

    public static float dpH3() {
        return getDp(R.dimen.h3);
    }

    public static float dpSmall() {
        return getDp(R.dimen.small);
    }

    public static float dpContent() {
        return getDp(R.dimen.content);
    }

    public static float dpLine() {
        return getDp(R.dimen.line);
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

    public static int screenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    public static float getDp(@DimenRes int resId) {
        return toDp(getResources().getDimensionPixelSize(resId));
    }

    private static Resources getResources() {
        return getContext().getResources();
    }

    private static Context getContext() {
        return Starter.getInstance().getContext();
    }
}
