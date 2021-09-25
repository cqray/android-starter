package cn.cqray.android.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;

import androidx.annotation.NonNull;

/**
 * @author Cqray
 * @date 2021/9/25 23:07
 */
public class DrawableUtils {


    public static Drawable createRippleDrawable(@NonNull Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[] {
                android.R.attr.actionBarItemBackground });
        Drawable drawable = ta.getDrawable(0);
        ta.recycle();
        return drawable;
    }

//    private Drawable createItemBackground() {
//        TypedArray ta = mContext.obtainStyledAttributes(new int[] {
//                android.R.attr.actionBarItemBackground });
//        Drawable drawable = ta.getDrawable(0);
//        ta.recycle();
//        return drawable;
//    }
}
