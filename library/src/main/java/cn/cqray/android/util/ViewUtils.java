package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 控件辅助工具
 * @author Cqray
 */
public class ViewUtils {

    /**
     * 通过View获取Activity
     * @param view 控件
     * @return Activity
     */
    @Nullable
    public static Activity getActivity(@NonNull View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 渲染界面
     * @param resId 界面资源ID
     * @return 界面
     */
    public static View inflate(@LayoutRes int resId) {
        Activity act = ActivityUtils.requireCurrent();
        ViewGroup root = act.findViewById(android.R.id.content);
        return LayoutInflater.from(act).inflate(resId, root, false);
    }

    public static void setDpMargin(@NonNull View view, float margin) {
        setDpMargin(view, margin, margin, margin, margin);
    }

    public static void setDpMargin(@NonNull View view, float l, float t, float r, float b) {
        setPxMargin(view, toPx(l), toPx(t), toPx(r), toPx(b));
    }

    public static void setPxMargin(@NonNull View view, int margin) {
        setPxMargin(view, margin, margin, margin, margin);
    }

    public static void setPxMargin(@NonNull View view, int l, int t, int r, int b) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.setMargins(l, t, r, b);
        view.requestLayout();
    }

    public static void setResMargin(@NonNull View view, int resId) {
        int m = resId == -1 || resId == 0 ? 0 : view.getResources().getDimensionPixelSize(resId);
        setPxMargin(view, m);
    }

    public static void setResMargin(@NonNull View view, int l, int t, int r, int b) {
        int lpx = l <= 0 ? 0 : view.getResources().getDimensionPixelSize(l);
        int tpx = t <= 0 ? 0 : view.getResources().getDimensionPixelSize(t);
        int rpx = r <= 0 ? 0 : view.getResources().getDimensionPixelSize(r);
        int bpx = b <= 0 ? 0 : view.getResources().getDimensionPixelSize(b);
        setPxMargin(view, lpx, tpx, rpx, bpx);
    }

    private static int toPx(float dp) {
        float tmp = Resources.getSystem().getDisplayMetrics().density * dp;
        return (int) (tmp >= 0 ? tmp + 0.5 : tmp - 0.5);
    }
}
