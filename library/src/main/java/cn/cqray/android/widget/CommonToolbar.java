package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import cn.cqray.android.R;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * 标题栏
 * @author Cqray
 */
public class CommonToolbar extends RelativeLayout {

    private int mPaddingLeft;
    private int mPaddingRight;
    private int mTitleSpace;

    private boolean mActionTextBold;
    private int mActionTextSize;
    private int mActionTextColor;
    private int mActionSpace;

    private Context mContext;
    private View mNavSpace;
    private TextView mNavText;
    private ImageView mNavIcon;
    private TextView mTitleView;
    private View mDividerView;
    private LinearLayout mNavLayout;
    private LinearLayout mActionLayout;
    private SparseArray<View> mViewArray;
    private SparseBooleanArray mVisibleArray;

    public CommonToolbar(Context context) {
        this(context, null, 0);
    }

    public CommonToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (getId() == NO_ID) {
            setId(R.id.starter_toolbar);
        }
        initInnerViews();
    }

    /**
     * 初始化控件
     */
    private void initInnerViews() {
        mViewArray = new SparseArray<>();
        mVisibleArray = new SparseBooleanArray();
        mActionTextBold = false;
        mActionTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.h2);
        mActionTextColor = Color.WHITE;
        mPaddingLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.content);
        mPaddingRight = mContext.getResources().getDimensionPixelOffset(R.dimen.content);
        mTitleSpace = mContext.getResources().getDimensionPixelOffset(R.dimen.content);

        // 设置布局
        LayoutInflater.from(getContext()).inflate(R.layout.starter_toolbar_layout, this, true);
        mNavLayout = findViewById(R.id._core_toolbar_nav_layout);
        mNavSpace = mNavLayout.getChildAt(1);
        mNavText = (TextView) mNavLayout.getChildAt(2);
        mNavIcon = (ImageView) mNavLayout.getChildAt(0);
        mActionLayout = findViewById(R.id._core_toolbar_action_layout);
        mDividerView = getChildAt(getChildCount() - 1);
        // 获取控件
        mTitleView = findViewById(R.id._core_toolbar_title);
    }

    private Drawable createItemBackground() {
        TypedArray ta = mContext.obtainStyledAttributes(new int[] {
                android.R.attr.actionBarItemBackground });
        Drawable drawable = ta.getDrawable(0);
        ta.recycle();
        return drawable;
    }

    private void refreshPadding() {
        boolean navVisible = mNavLayout.getVisibility() == VISIBLE;
        mNavLayout.setPadding(mPaddingLeft, 0, navVisible ? mTitleSpace : 0, 0);
        mNavSpace.getLayoutParams().width = mPaddingLeft;
        mActionLayout.setPadding(0, 0, mPaddingRight, 0);
        mTitleView.setPadding(navVisible ? 0 : mTitleSpace, 0, mTitleSpace, 0);
    }

    /** 设置所有的间隔 */
    public CommonToolbar setPadding(float left, float right) {
        mPaddingLeft = toPx(left);
        mPaddingRight = toPx(right);
        refreshPadding();
        return this;
    }

    /** 标题居中 **/
    public CommonToolbar setTitleCenter() {
        if (mTitleView.getGravity() != Gravity.CENTER) {
            LayoutParams params = (LayoutParams) mTitleView.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.START_OF, -1);
                params.addRule(RelativeLayout.END_OF, -1);
            } else {
                params.addRule(RelativeLayout.LEFT_OF, -1);
                params.addRule(RelativeLayout.RIGHT_OF, -1);
            }
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mTitleView.setGravity(Gravity.CENTER);
        }
        return this;
    }

    /** 标题居左 **/
    public CommonToolbar setTitleLeft() {
        if (mTitleView.getGravity() == Gravity.CENTER) {
            LayoutParams params = (LayoutParams) mTitleView.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.START_OF, R.id._core_toolbar_action_layout);
                params.addRule(RelativeLayout.END_OF, R.id._core_toolbar_nav_layout);
            } else {
                params.addRule(RelativeLayout.LEFT_OF, R.id._core_toolbar_action_layout);
                params.addRule(RelativeLayout.RIGHT_OF, R.id._core_toolbar_nav_layout);
            }
            params.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
            mTitleView.setGravity(Gravity.CENTER_VERTICAL);
        }
        return this;
    }

    /** 是否支持水波纹 **/
    public CommonToolbar setRippleEnable(boolean rippleEnable) {
        boolean enable = mNavIcon.getBackground() != null;
        if (rippleEnable == enable) {
            return this;
        }
        boolean hasBackground = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && rippleEnable;
        ViewCompat.setBackground(mNavIcon, hasBackground ? createItemBackground() : null);
        ViewCompat.setBackground(mNavText, hasBackground ? createItemBackground() : null);
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            ViewCompat.setBackground(view, hasBackground ? createItemBackground() : null);
        }
        return this;
    }

    /////////////////////////左部份//////////////////////////////

    public CommonToolbar setNavSpace(float space) {
        mNavSpace.getLayoutParams().width = toPx(space);
        return this;
    }

    public CommonToolbar setNavIcon(@DrawableRes int resId) {
        mNavIcon.setImageResource(resId);
        return this;
    }

    public CommonToolbar setNavIcon(Drawable drawable) {
        mNavIcon.setImageDrawable(drawable);
        return this;
    }

    public CommonToolbar setNavIconVisible(boolean visible) {
        boolean textVisible = mNavIcon.getVisibility() == VISIBLE;
        mNavIcon.setVisibility(visible ? VISIBLE : GONE);
        mNavSpace.setVisibility(visible && textVisible ? VISIBLE : GONE);
        mNavLayout.setVisibility(visible && textVisible ? VISIBLE : GONE);
        refreshPadding();
        return this;
    }

    public CommonToolbar setNavText(CharSequence text) {
        mNavText.setText(text);
        return this;
    }

    public CommonToolbar setNavText(@StringRes int id) {
        mNavText.setText(id);
        return this;
    }

    public CommonToolbar setNavTextColor(int color) {
        mNavText.setTextColor(color);
        return this;
    }

    public CommonToolbar setNavTextSize(float size) {
        mNavText.setTextSize(size);
        return this;
    }

    public CommonToolbar setNavTextBold(boolean bold) {
        mNavText.setTypeface(Typeface.defaultFromStyle(bold ? Typeface.BOLD : Typeface.NORMAL));
        return this;
    }

    public CommonToolbar setNavTextVisible(boolean visible) {
        boolean iconVisible = mNavIcon.getVisibility() == VISIBLE;
        mNavText.setVisibility(visible ? VISIBLE : GONE);
        mNavSpace.setVisibility(visible && iconVisible ? VISIBLE : GONE);
        mNavLayout.setVisibility(visible && iconVisible ? VISIBLE : GONE);
        refreshPadding();
        return this;
    }

    public CommonToolbar setNavListener(OnClickListener listener) {
        mNavIcon.setOnClickListener(listener);
        mNavText.setOnClickListener(listener);
        mNavLayout.setOnClickListener(listener);
        return this;
    }

    /////////////////////////标题部分/////////////////////////////

    public CommonToolbar setTitle(CharSequence title) {
        mTitleView.setText(title);
        return this;
    }

    public CommonToolbar setTitle(int id) {
        mTitleView.setText(id);
        return this;
    }

    public CommonToolbar setTitleTextColor(int color) {
        mTitleView.setTextColor(color);
        return this;
    }

    public CommonToolbar setTitleTextSize(float size) {
        mTitleView.setTextSize(size);
        return this;
    }

    public CommonToolbar setTitleTextBold(boolean bold) {
        mTitleView.setTypeface(Typeface.defaultFromStyle(bold ? Typeface.BOLD : Typeface.NORMAL));
        return this;
    }

    public CommonToolbar setTitleSpace(float sapce) {
        mTitleSpace = toPx(sapce);
        refreshPadding();
        return this;
    }

    public CommonToolbar setActionText(int key, CharSequence text) {
        int index = mViewArray.size();
        View view = mViewArray.get(key);
        if (view != null) {
            index = mViewArray.indexOfKey(key);
            mViewArray.remove(key);
            mActionLayout.removeView(view);
        }
        TextView tv = new AppCompatTextView(mContext);
        tv.setText(text);
        tv.setTextSize(COMPLEX_UNIT_PX, mActionTextSize);
        tv.setTextColor(mActionTextColor);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new MarginLayoutParams(-2, -1));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setPadding(mActionSpace, 0, mActionSpace, 0);
        tv.setVisibility(mVisibleArray.get(key) ? VISIBLE : GONE);
        tv.setTypeface(Typeface.defaultFromStyle(mActionTextBold ? Typeface.BOLD : Typeface.NORMAL));
        if (mNavIcon.getBackground() != null) {
            ViewCompat.setBackground(tv, createItemBackground());
        }
        mViewArray.put(key, tv);
        mActionLayout.addView(tv, index);
        return this;
    }

    public CommonToolbar setActionText(int key, int id) {
        return setActionText(key, mContext.getString(id));
    }

    public CommonToolbar setActionTextColor(int color) {
        mActionTextColor = color;
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }
        return this;
    }

    public CommonToolbar setActionTextColor(int key, int color) {
        View view = mViewArray.get(key);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
        return this;
    }

    public CommonToolbar setActionTextSize(float size) {
        mActionTextSize = toPx(size);
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextSize(size);
            }
        }
        return this;
    }

    public CommonToolbar setActionTextSize(int key, float size) {
        View view = mViewArray.get(key);
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(size);
        }
        return this;
    }

    public CommonToolbar setActionTextBold(boolean bold) {
        mActionTextBold = bold;
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(Typeface.defaultFromStyle(bold ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
        return this;
    }

    public CommonToolbar setActionTextBold(int key, boolean bold) {
        View view = mViewArray.get(key);
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(Typeface.defaultFromStyle(bold ? Typeface.BOLD : Typeface.NORMAL));
        }
        return this;
    }

    public CommonToolbar setActionIcon(int key, @DrawableRes int resId) {
        int index = mViewArray.size();
        View view = mViewArray.get(key);
        if (view != null) {
            index = mViewArray.indexOfKey(key);
            mViewArray.remove(key);
            mActionLayout.removeView(view);
        }
        ImageView iv = new AppCompatImageView(mContext);
        iv.setImageResource(resId);
        iv.setLayoutParams(new MarginLayoutParams(-2, -1));
        iv.setClickable(true);
        iv.setFocusable(true);
        iv.setPadding(mActionSpace, 0, mActionSpace, 0);
        iv.setVisibility(mVisibleArray.get(key) ? VISIBLE : GONE);
        if (mNavIcon.getBackground() != null) {
            ViewCompat.setBackground(iv, createItemBackground());
        }
        mViewArray.put(key, iv);
        mActionLayout.addView(iv, index);
        return this;
    }

    public CommonToolbar setActionVisible(int key, boolean visible) {
        mVisibleArray.put(key, visible);
        View view = mViewArray.get(key);
        if (view != null) {
            view.setVisibility(visible ? VISIBLE : GONE);
        }
        return this;
    }

    public CommonToolbar setActionSpace(float space) {
        mActionSpace = toPx(space);
        for (int i = 0; i < mViewArray.size(); i++) {
            View view = mViewArray.valueAt(i);
            view.setPadding(mActionSpace, 0, mActionSpace, 0);
        }
        mActionLayout.setPadding(0, 0, mTitleSpace - mActionSpace / 2 , 0);
        return this;
    }

    public CommonToolbar setActionIconListener(int key, OnClickListener listener) {
        View view = mViewArray.get(key);
        if (view != null) {
            if (view instanceof ImageView) {
                view.setOnClickListener(listener);
            }
        }
        return this;
    }

    ///////////////////////////分割线//////////////////////////////

    public CommonToolbar setDividerColor(int color) {
        mDividerView.setBackgroundColor(color);
        return this;
    }

    public CommonToolbar setDividerHeight(float height) {
        mDividerView.getLayoutParams().height = toPx(height);
        return this;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public ImageView getNavIconView() {
        return mNavIcon;
    }

    public TextView getNavTextView() {
        return mNavText;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getActionView(int key) {
        return (T) mViewArray.get(key);
    }

    private int toPx(float dip) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) Math.ceil(dip * density);
    }
}
