package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.ripple.RippleDrawableCompat;
import com.google.android.material.ripple.RippleUtils;

import cn.cqray.android.R;
import cn.cqray.android.util.DrawableUtils;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Action布局
 * @author Cqray
 */
public class ActionLayout extends LinearLayout {

    private int mSpace;
    private int mTextSize;
    private int mTextColor;
    private boolean mTextBold;
    private boolean mRippleEnable = true;
    private final SparseArray<View> mViewArray = new SparseArray<>();
    private final SparseBooleanArray mVisibleArray = new SparseBooleanArray();

    public ActionLayout(Context context) {
        this(context, null);
    }

    public ActionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpace = context.getResources().getDimensionPixelSize(R.dimen.small);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.body);
        mTextColor = ContextCompat.getColor(context, R.color.text);
        setPadding(mSpace, 0, mSpace, 0);
        setOrientation(HORIZONTAL);
//        setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_MIDDLE|LinearLayoutCompat.SHOW_DIVIDER_BEGINNING|LinearLayoutCompat.SHOW_DIVIDER_END);
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(Color.TRANSPARENT);
//        drawable.setSize(60, 60);
//        setDividerDrawable(drawable);
//        setDividerPadding(60);
//    }

    public ActionLayout setText(int key, CharSequence text) {
        int index = mViewArray.size();
        View view = mViewArray.get(key);
        if (view != null) {
            index = mViewArray.indexOfKey(key);
            mViewArray.remove(key);
            removeView(view);
        }
        TextView tv = new AppCompatTextView(getContext());
        tv.setText(text);
        tv.setTextSize(COMPLEX_UNIT_PX, mTextSize);
        tv.setTextColor(mTextColor);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(mSpace, 0, mSpace, 0);
        tv.setLayoutParams(new MarginLayoutParams(-2, -1));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setVisibility(mVisibleArray.get(key) ? VISIBLE : GONE);
        tv.setTypeface(Typeface.defaultFromStyle(mTextBold ? Typeface.BOLD : Typeface.NORMAL));
        if (mRippleEnable) {
            ViewCompat.setBackground(tv, generateRippleDrawable());
        }
        mViewArray.put(key, tv);
        addView(tv, index);
        return this;
    }

    public ActionLayout setIcon(int key, @DrawableRes int resId) {
        int index = mViewArray.size();
        View view = mViewArray.get(key);
        if (view != null) {
            index = mViewArray.indexOfKey(key);
            mViewArray.remove(key);
            removeView(view);
        }
        ImageView iv = new AppCompatImageView(getContext());
        iv.setImageResource(resId);
        iv.setLayoutParams(new MarginLayoutParams(-2, -1));
        iv.setClickable(true);
        iv.setFocusable(true);
        iv.setPadding(mSpace, 0, mSpace, 0);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setVisibility(mVisibleArray.get(key) ? VISIBLE : GONE);
        // Api 19一下设置
        if (mRippleEnable) {
            ViewCompat.setBackground(iv, generateRippleDrawable());
        }
        mViewArray.put(key, iv);
        addView(iv, index);
        return this;
    }

    public ActionLayout setActionVisible(int key, boolean visible) {
        mVisibleArray.put(key, visible);
        View view = mViewArray.get(key);
        if (view != null) {
            view.setVisibility(visible ? VISIBLE : GONE);
        }
        return this;
    }


    public Drawable generateRippleDrawable() {
        Context context = getContext();
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{
                    android.R.attr.actionBarItemBackground});
            drawable = ta.getDrawable(0);
            ta.recycle();
        } else {
            drawable = ContextCompat.getDrawable(context, R.drawable.bg_ripple);
        }
        return drawable;
    }
}
