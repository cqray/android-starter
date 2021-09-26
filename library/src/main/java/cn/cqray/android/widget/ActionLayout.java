package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;
import cn.cqray.android.util.ViewUtils;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Action布局控件
 * @author Cqray
 */
public class ActionLayout extends LinearLayout {

    /** 左间隔 **/
    private Space mLeftSpace;
    /** 右间隔 **/
    private Space mRightSpace;
    /** 间隔 **/
    private int mActionSpace;
    /** 文字大小 **/
    private int mActionTextSize;
    /** 文字颜色 **/
    private int mActionTextColor;
    /** 文本样式 **/
    private int mActionTextStyle;
    /** 是否显示水波纹 **/
    private boolean mUseRipple;
    /** 控件列表 **/
    private final SparseArray<View> mViewArray = new SparseArray<>();
    /** 控件是否显示列表 **/
    private final SparseBooleanArray mVisibleArray = new SparseBooleanArray();

    public ActionLayout(Context context) {
        this(context, null);
    }

    public ActionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ActionLayout);
        mActionSpace = ta.getDimensionPixelSize(R.styleable.ActionLayout_alActionSpace,
                getResources().getDimensionPixelSize(R.dimen.content)) / 2;
        mActionTextSize = ta.getDimensionPixelSize(R.styleable.ActionLayout_alActionTextSize,
                getResources().getDimensionPixelSize(R.dimen.body));
        mActionTextColor = ta.getColor(R.styleable.ActionLayout_alActionTextColor,
                ContextCompat.getColor(context, R.color.text));
        mActionTextStyle = ta.getInt(R.styleable.ActionLayout_alActionTextStyle, 0);
        mUseRipple = ta.getBoolean(R.styleable.ActionLayout_alUseRipple, true);
        ta.recycle();
        mLeftSpace = new Space(context);
        mRightSpace = new Space(context);
        addView(mLeftSpace);
        addView(mRightSpace);
        setActionSpace(Float.MIN_VALUE);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // Not do anything.
    }

    public ActionLayout setActionText(int key, @StringRes int resId) {
        return setActionText(key, getResources().getString(resId));
    }

    public ActionLayout setActionText(int key, CharSequence text) {
        int index = indexOf(key);
        boolean horizontal = getOrientation() == HORIZONTAL;
        TextView tv = new AppCompatTextView(getContext());
        tv.setText(text);
        tv.setTextSize(COMPLEX_UNIT_PX, mActionTextSize);
        tv.setTextColor(mActionTextColor);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(mActionSpace, 0, mActionSpace, 0);
        tv.setLayoutParams(new MarginLayoutParams(horizontal ? -2 : -1, horizontal ? -1 : -2));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setVisibility(mVisibleArray.get(key) ? VISIBLE : GONE);
        tv.setTypeface(Typeface.defaultFromStyle(mActionTextStyle));
        addView(tv, index);
        ViewUtils.setRippleBackground(tv, mUseRipple);
        mViewArray.put(key, tv);
        return this;
    }

    public ActionLayout setActionTextColor(int color) {
        mActionTextColor = color;
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setText(mActionTextColor);
            }
        }
        return this;
    }

    public ActionLayout setActionTextColor(int key, int color) {
        View view = mViewArray.get(key);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
        return this;
    }

    public ActionLayout setActionTextSize(float size) {
        mActionTextSize = toPx(size);
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextSize(COMPLEX_UNIT_PX, mActionTextSize);
            }
        }
        return this;
    }

    public ActionLayout setActionTextSize(int key, float size) {
        View view = mViewArray.get(key);
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(size);
        }
        return this;
    }

    public ActionLayout setActionTypeface(Typeface typeface) {
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
        return this;
    }

    public ActionLayout setActionTypeface(int key, Typeface typeface) {
        View view = mViewArray.get(key);
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeface);
        }
        return this;
    }

    public ActionLayout setActionIcon(int key, @DrawableRes int resId) {
        return setActionIcon(key, ContextCompat.getDrawable(getContext(), resId));
    }

    public ActionLayout setActionIcon(int key, Drawable drawable) {
        int index = indexOf(key);
        boolean horizontal = getOrientation() == HORIZONTAL;
        ImageView iv = new AppCompatImageView(getContext());
        iv.setImageDrawable(drawable);
        iv.setLayoutParams(new MarginLayoutParams(horizontal ? -2 : -1, horizontal ? -1 : -2));
        iv.setClickable(true);
        iv.setFocusable(true);
        iv.setPadding(mActionSpace, 0, mActionSpace, 0);
        iv.setVisibility(mVisibleArray.get(key) ? VISIBLE : GONE);
        addView(iv, index);
        ViewUtils.setRippleBackground(iv, mUseRipple);
        mViewArray.put(key, iv);
        return this;
    }

    public ActionLayout setActionVisible(boolean visible) {
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view != null) {
                view.setVisibility(visible ? VISIBLE : GONE);
            }
        }
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

    public ActionLayout setUseRipple(boolean useRipple) {
        mUseRipple = useRipple;
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            ViewUtils.setRippleBackground(view, mUseRipple);
        }
        return this;
    }

    public ActionLayout setUseRipple(int key, boolean useRipple) {
        mUseRipple = useRipple;
        View view = mViewArray.get(key);
        if (view != null) {
            ViewUtils.setRippleBackground(view, mUseRipple);
        }
        return this;
    }

    public ActionLayout setActionSpace(float space) {
        if (space != Float.MIN_VALUE) {
            mActionSpace = toPx(space / 2);
        }
        boolean horizontal = getOrientation() == HORIZONTAL;
        mLeftSpace.getLayoutParams().width = horizontal ? mActionSpace : 0;
        mLeftSpace.getLayoutParams().height = !horizontal ? mActionSpace : 0;
        mRightSpace.getLayoutParams().width = horizontal ? mActionSpace : 0;
        mRightSpace.getLayoutParams().height = !horizontal ? mActionSpace : 0;
        mLeftSpace.requestLayout();
        mRightSpace.requestLayout();
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            view.setPadding(
                    horizontal ? mActionSpace : 0,
                    !horizontal ? mActionSpace : 0,
                    horizontal ? mActionSpace : 0,
                    !horizontal ? mActionSpace : 0);
        }
        return this;
    }

    public ActionLayout setActionListener(int key, OnClickListener listener) {
        View view = mViewArray.get(key);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getActionView(int key) {
        return (T) mViewArray.get(key);
    }

    public int getActionSpace() {
        return mActionSpace * 2;
    }

    /**
     * 获取对应键值的控件索引
     * @param key 键值
     */
    private int indexOf(int key) {
        int index = mViewArray.size();
        View view = mViewArray.get(key);
        if (view != null) {
            // 对应键值已添加过控件，则移除控件
            index = mViewArray.indexOfKey(key);
            mViewArray.remove(key);
            removeView(view);
        } else {
            // 未添加做控件，则直接设置为可见
            mVisibleArray.put(key, true);
        }
        return index + 1;
    }

    private int toPx(float dip) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dip * density +0.5f);
    }
}
