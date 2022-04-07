package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;

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
        Activity act = ActivityUtils.getTopActivity();
        if (act != null) {
            ViewGroup root = act.findViewById(android.R.id.content);
            return LayoutInflater.from(act).inflate(resId, root, false);
        } else {
            return LayoutInflater.from(Utils.getApp()).inflate(resId, null, false);
        }
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

    public static void setRippleBackground(@NonNull View view, boolean rippleEnable) {
        Context context = view.getContext();
        if (rippleEnable) {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{
                        android.R.attr.actionBarItemBackground});
                drawable = ta.getDrawable(0);
                ta.recycle();
            }
            ViewCompat.setBackground(view, drawable);
        } else {
            ViewCompat.setBackground(view, null);
        }
    }

    public static void setElevation(@NonNull View view, float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevation);
        }
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            ViewCompat.setBackground(view, createMaterialShapeDrawableBackground(view.getContext(), background));
        }

        MaterialShapeUtils.setParentAbsoluteElevation(view);
        MaterialShapeUtils.setElevation(view, elevation);
    }

    public static void setOverScrollMode(View view, int overScrollMode) {
        if (view instanceof ViewPager2) {
            View child = ((ViewPager2) view).getChildAt(0);
            if (child instanceof RecyclerView) {
                child.setOverScrollMode(overScrollMode);
            }
        } else if (view != null) {
            view.setOverScrollMode(overScrollMode);
        }
    }

    @NonNull
    private static MaterialShapeDrawable createMaterialShapeDrawableBackground(Context context, Drawable background) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        if (background instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(
                    ColorStateList.valueOf(((ColorDrawable) background).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(context);
        return materialShapeDrawable;
    }

    private static int toPx(float dp) {
        float tmp = Resources.getSystem().getDisplayMetrics().density * dp;
        return (int) (tmp >= 0 ? tmp + 0.5 : tmp - 0.5);
    }
}
