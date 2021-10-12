package cn.cqray.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;

import cn.cqray.android.R;

/**
 * @author Cqray
 * @date 2021/9/24 21:58
 */
public class Toolbar extends RelativeLayout {

    /** 标题控件 **/
    private TextView mTitleView;
    private View mDividerView;
    private IconTextLayout mBackLayout;
    private ActionLayout mActionLayout;

    private int mTitleSpace;
    private boolean mTitleCenter;
    private LifecycleRegistry mLifecycleRegistry;
    private LifecycleOwner mLifecycleOwner;
    private final MutableLiveData<Integer> mPadding = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUseRipple = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mTitleCenterData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mTitleEditable = new MutableLiveData<>();

    public Toolbar(@NonNull Context context) {
        this(context, null);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLifecycleOwner = () -> mLifecycleRegistry;
        mLifecycleRegistry = new LifecycleRegistry(mLifecycleOwner);
        mLifecycleRegistry.setCurrentState(Lifecycle.State.INITIALIZED);
        // 设置属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        int padding = ta.getDimensionPixelSize(R.styleable.Toolbar_tbPadding, getResources().getDimensionPixelSize(R.dimen.content));
        float elevation = ta.getDimension(R.styleable.Toolbar_tbElevation, getResources().getDimension(R.dimen.elevation));
        boolean useRipple = ta.getBoolean(R.styleable.Toolbar_tbUseRipple, true);
        boolean titleEditable = ta.getBoolean(R.styleable.Toolbar_tbTitleEditable, false);
        mTitleCenter = ta.getBoolean(R.styleable.Toolbar_tbTitleCenter, false);
        mTitleSpace = ta.getDimensionPixelSize(R.styleable.Toolbar_tbTitleSpace, getResources().getDimensionPixelSize(R.dimen.content));
        ta.recycle();
        int primaryColor = ContextCompat.getColor(context, R.color.colorPrimary);
        Drawable background = getBackground() == null ? new ColorDrawable(primaryColor) : getBackground();
        // 设置MaterialShapeDrawable
        if (background instanceof ColorDrawable) {
            ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(background));
        }
        // 设置默认Id
        if (getId() == NO_ID) {
            setId(R.id.starter_toolbar);
        }

        mTitleCenterData.postValue(mTitleCenter);
        mTitleEditable.setValue(titleEditable);
        mUseRipple.setValue(useRipple);
        mPadding.setValue(padding);

        setElevation(elevation);
        initToolbarBack(attrs);
        initToolbarAction(attrs);
        initToolbarTittle(attrs);
        initToolbarDivider(attrs);
        initLiveData();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
        MaterialShapeUtils.setParentAbsoluteElevation(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    private void initToolbarBack(AttributeSet attrs) {
        Context context = getContext();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        Drawable drawable = ta.getDrawable(R.styleable.Toolbar_tbBackIcon);
        String text = ta.getString(R.styleable.Toolbar_tbBackText);
        boolean iconVisible = ta.getBoolean(R.styleable.Toolbar_tbBackIconVisible, true);
        int backTextColor = ta.getColor(R.styleable.Toolbar_tbBackTextColor, Color.WHITE);
        int backTextSize = ta.getDimensionPixelSize(R.styleable.Toolbar_tbBackTextSize, getResources().getDimensionPixelSize(R.dimen.h3));
        int backTextStyle = ta.getInt(R.styleable.Toolbar_tbBackTextStyle, 0);
        ta.recycle();
        // 设置Nav布局
        LayoutParams params = new LayoutParams(-2, -1);
        mBackLayout = new IconTextLayout(context);
        mBackLayout.setId(R.id.starter_toolbar_back_layout);
        mBackLayout.setLayoutParams(params);
        mBackLayout.setText(text);
        mBackLayout.setTextColor(backTextColor);
        mBackLayout.setTextSize(TypedValue.COMPLEX_UNIT_PX, backTextSize);
        mBackLayout.setTypeface(Typeface.defaultFromStyle(backTextStyle));
        mBackLayout.getIconView().setVisibility(iconVisible ? VISIBLE : GONE);
        if (drawable == null) {
            mBackLayout.setIconResource(R.drawable.def_back_material_light);
        } else {
            mBackLayout.setIconDrawable(drawable);
        }
        addView(mBackLayout);
    }

    private void initToolbarAction(AttributeSet attrs) {
        Context context = getContext();
        float density = getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        int actionTextColor = ta.getColor(R.styleable.Toolbar_tbActionTextColor, Color.WHITE);
        int actionTextSize = ta.getDimensionPixelSize(R.styleable.Toolbar_tbActionTextSize, getResources().getDimensionPixelSize(R.dimen.h3));
        int actionTextStyle = ta.getInt(R.styleable.Toolbar_tbActionTextStyle, 0);
        int actionSpace = ta.getDimensionPixelSize(R.styleable.Toolbar_tbActionSpace, getResources().getDimensionPixelSize(R.dimen.content));
        ta.recycle();
        // 设置Action布局
        LayoutParams params = new LayoutParams(-2, -1);
        params.addRule(Build.VERSION.SDK_INT >= 17 ? ALIGN_PARENT_END : ALIGN_PARENT_RIGHT);
        mActionLayout = new ActionLayout(context);
        mActionLayout.setId(R.id.starter_toolbar_action_layout);
        mActionLayout.setLayoutParams(params);
        mActionLayout.setActionTextColor(actionTextColor);
        mActionLayout.setActionTextSize(actionTextSize / density);
        mActionLayout.setActionTypeface(Typeface.defaultFromStyle(actionTextStyle));
        mActionLayout.setActionSpace(actionSpace / density);
        addView(mActionLayout);
        mActionLayout.requestLayout();
    }

    private void initToolbarTittle(AttributeSet attrs) {
        Context context = getContext();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        String titleText = ta.getString(R.styleable.Toolbar_tbTitleText);
        int titleTextColor = ta.getColor(R.styleable.Toolbar_tbTitleTextColor, Color.WHITE);
        int titleTextSize = ta.getDimensionPixelSize(R.styleable.Toolbar_tbTitleTextSize, getResources().getDimensionPixelSize(R.dimen.h2));
        int titleTextStyle = ta.getInt(R.styleable.Toolbar_tbTitleTextStyle, 0);
        ta.recycle();
        // 设置标题
        mTitleView = new AppCompatEditText(context);
        mTitleView.setLayoutParams(new LayoutParams(-1, -1));
        mTitleView.setId(R.id.starter_toolbar_title);
        mTitleView.setText(titleText);
        mTitleView.setTextColor(titleTextColor);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        mTitleView.setTypeface(Typeface.defaultFromStyle(titleTextStyle));
        addView(mTitleView);
    }

    private void initToolbarDivider(AttributeSet attrs) {
        Context context = getContext();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        int dividerColor = ta.getColor(R.styleable.Toolbar_tbDividerColor, ContextCompat.getColor(context, R.color.divider));
        int dividerHeight = ta.getDimensionPixelSize(R.styleable.Toolbar_tbDividerHeight, 0);
        int dividerMargin = ta.getDimensionPixelSize(R.styleable.Toolbar_tbDividerMargin, 0);
        boolean dividerVisible = ta.getBoolean(R.styleable.Toolbar_tbDividerVisible, true);
        ta.recycle();
        // 设置分割线
        LayoutParams params = new LayoutParams(-1, dividerHeight);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.setMargins(dividerMargin, 0, dividerMargin, 0);
        mDividerView = new View(context);
        mDividerView.setBackgroundColor(dividerColor);
        mDividerView.setLayoutParams(params);
        mDividerView.setVisibility(dividerVisible ? VISIBLE : INVISIBLE);
        addView(mDividerView);
    }

    @SuppressLint("InlinedApi")
    private void initLiveData() {
        mTitleCenterData.observe(mLifecycleOwner, center -> {
            boolean isNewApi = Build.VERSION.SDK_INT >= 17;
            int padding = mPadding.getValue() == null ? 0 : mPadding.getValue();
            boolean iconVisible = mBackLayout.getIconView().getVisibility() == View.VISIBLE;
            LayoutParams params = (LayoutParams) mTitleView.getLayoutParams();
            if (center) {
                int leftWidth = mBackLayout.getWidth() + (iconVisible ? mTitleSpace - padding : 0);
                int actionWidth = mActionLayout.getWidth() + mTitleSpace - mActionLayout.getActionSpace();
                int m = Math.max(leftWidth, actionWidth);
                params.addRule(isNewApi ? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, -1);
                params.addRule(isNewApi ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, -1);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.setMargins(m, 0, m, 0);
                mTitleView.setGravity(Gravity.CENTER);
            } else {
                params.addRule(isNewApi ? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, R.id.starter_toolbar_action_layout);
                params.addRule(isNewApi ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, R.id.starter_toolbar_back_layout);
                params.leftMargin = iconVisible ? mTitleSpace - padding : 0;
                params.rightMargin = mTitleSpace - mActionLayout.getActionSpace();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(iconVisible ? mTitleSpace - padding : 0);
                    params.setMarginEnd(mTitleSpace - mActionLayout.getActionSpace());
                }
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                mTitleView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
            mTitleView.setLayoutParams(params);
        });
        mTitleEditable.observe(mLifecycleOwner, aBoolean -> {
            mTitleView.setFocusableInTouchMode(aBoolean);
            mTitleView.setClickable(aBoolean);
            mTitleView.setFocusable(aBoolean);
            mTitleView.setEnabled(aBoolean);
            if (aBoolean) {
                TypedArray ta = getContext().obtainStyledAttributes(new int[]{
                        android.R.attr.editTextBackground});
                Drawable drawable = ta.getDrawable(0);
                ta.recycle();
                ViewCompat.setBackground(mTitleView, drawable);
            } else {
                ViewCompat.setBackground(mTitleView, null);
            }
        });
        mUseRipple.observe(mLifecycleOwner, aBoolean -> {
            mBackLayout.setUseRipple(aBoolean);
            mActionLayout.setUseRipple(aBoolean);
        });
        mPadding.observe(mLifecycleOwner, aInteger -> {
            // 设置BackLayout内部间隔
            boolean iconVisible = mBackLayout.getIconView().getVisibility() == View.VISIBLE;
            mBackLayout.setPadding(aInteger, 0, iconVisible ? aInteger : 0, 0);
            // 设置ActionLayout右部间隔
            LayoutParams params = (LayoutParams) mActionLayout.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(aInteger - mActionLayout.getActionSpace());
            }
            params.rightMargin = aInteger - mActionLayout.getActionSpace();
        });
    }

    @Override
    public void setElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(elevation);
        }
        MaterialShapeUtils.setElevation(this, elevation);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {}

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {}

    @Override
    public void setGravity(int gravity) {}

    public Toolbar setPadding(float padding) {
        mPadding.setValue(toPx(padding));
        return this;
    }

    public Toolbar setUseRipple(boolean useRipple) {
        mUseRipple.setValue(useRipple);
        return this;
    }

    public Toolbar setTitleCenter(boolean center) {
        mTitleCenterData.postValue(center);
        return this;
    }

    public Toolbar setBackSpace(float space) {
        mBackLayout.setSpace(space);
        return this;
    }

    public Toolbar setBackIcon(@DrawableRes int resId) {
        mBackLayout.setIconResource(resId);
        return this;
    }

    public Toolbar setBackIcon(Drawable drawable) {
        mBackLayout.setIconDrawable(drawable);
        return this;
    }

    public Toolbar setBackIcon(Bitmap bitmap) {
        mBackLayout.setIconBitmap(bitmap);
        return this;
    }

    public Toolbar setBackIconTintColor(int color) {
        mBackLayout.setIconTintColor(color);
        return this;
    }

    public Toolbar setBackIconTintList(ColorStateList tintList) {
        mBackLayout.setIconTintList(tintList);
        return this;
    }

    public Toolbar setBackIconVisible(boolean visible) {
        if (visible != (mBackLayout.getIconView().getVisibility() == VISIBLE)) {
            mBackLayout.getIconView().setVisibility(visible ? VISIBLE : GONE);
            mPadding.setValue(mPadding.getValue());
        }
        return this;
    }

    public Toolbar setBackText(CharSequence text) {
        mBackLayout.setText(text);
        return this;
    }

    public Toolbar setBackText(@StringRes int id) {
        mBackLayout.setText(id);
        return this;
    }

    public Toolbar setBackTextColor(int color) {
        mBackLayout.setTextColor(color);
        return this;
    }

    public Toolbar setBackTextSize(float size) {
        mBackLayout.setTextSize(size);
        return this;
    }

    public Toolbar setBackTypeface(Typeface typeface) {
        mBackLayout.setTypeface(typeface);
        return this;
    }

    public Toolbar setBackListener(OnClickListener listener) {
        mBackLayout.setOnClickListener(listener);
        return this;
    }

    public Toolbar setTitle(@StringRes int id) {
        mTitleView.setText(id);
        return this;
    }

    public Toolbar setTitle(CharSequence text) {
        mTitleView.setText(text);
        return this;
    }

    public Toolbar setTitleTextColor(int color) {
        mTitleView.setTextColor(color);
        return this;
    }

    public Toolbar setTitleTextSize(float size) {
        mTitleView.setTextSize(size);
        return this;
    }

    public Toolbar setTitleTypeface(Typeface typeface) {
        mTitleView.setTypeface(typeface);
        return this;
    }

    public Toolbar setTitleEditable(boolean editable) {
        mTitleEditable.setValue(editable);
        return this;
    }

    public Toolbar setTitleSpace(float space) {
        mTitleSpace = toPx(space);
        mTitleCenterData.postValue(mTitleCenter);
        return this;
    }

    public Toolbar setActionText(int key, CharSequence text) {
        mActionLayout.setActionText(key, text);
        return this;
    }

    public Toolbar setActionText(int key, int id) {
        return setActionText(key, mActionLayout.getContext().getString(id));
    }

    public Toolbar setActionTextColor(int color) {
        mActionLayout.setActionTextColor(color);
        return this;
    }

    public Toolbar setActionTextColor(int key, int color) {
        mActionLayout.setActionTextColor(key, color);
        return this;
    }

    public Toolbar setActionTextSize(float size) {
        mActionLayout.setActionTextSize(size);
        return this;
    }

    public Toolbar setActionTextSize(int key, float size) {
        mActionLayout.setActionTextSize(key, size);
        return this;
    }

    public Toolbar setActionTypeface(Typeface typeface) {
        mActionLayout.setActionTypeface(typeface);
        return this;
    }

    public Toolbar setActionTypeface(int key, Typeface typeface) {
        mActionLayout.setActionTypeface(key, typeface);
        return this;
    }

    public Toolbar setActionIcon(int key, @DrawableRes int resId) {
        mActionLayout.setActionIcon(key, resId);
        return this;
    }

    public Toolbar setActionIcon(int key, Drawable drawable) {
        mActionLayout.setActionIcon(key, drawable);
        return this;
    }

    public Toolbar setActionVisible(boolean visible) {
        mActionLayout.setActionVisible(visible);
        return this;
    }

    public Toolbar setActionVisible(int key, boolean visible) {
        mActionLayout.setActionVisible(key, visible);
        return this;
    }

    public Toolbar setActionUseRipple(int key, boolean useRipple) {
        mActionLayout.setUseRipple(key, useRipple);
        return this;
    }

    public Toolbar setActionSpace(float space) {
        mActionLayout.setActionSpace(space);
        mPadding.setValue(mPadding.getValue());
        mTitleCenterData.postValue(mTitleCenter);
        return this;
    }

    public Toolbar setActionListener(int key, OnClickListener listener) {
        mActionLayout.setActionListener(key, listener);
        return this;
    }

    public Toolbar setDividerColor(int color) {
        mDividerView.setBackgroundColor(color);
        return this;
    }

    public Toolbar setDividerHeight(float height) {
        mDividerView.getLayoutParams().height = toPx(height);
        mDividerView.requestLayout();
        return this;
    }

    public Toolbar setDividerMargin(float margin) {
        int m = toPx(margin);
        LayoutParams params = (LayoutParams) mDividerView.getLayoutParams();
        params.setMargins(m, 0, m, 0);
        mDividerView.requestLayout();
        return this;
    }

    public Toolbar setDividerVisible(boolean visible) {
        mDividerView.setVisibility(visible ? VISIBLE : INVISIBLE);
        return this;
    }

    public IconTextLayout getBackLayout() {
        return mBackLayout;
    }

    public ActionLayout getActionLayout() {
        return mActionLayout;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    @NonNull
    private MaterialShapeDrawable createMaterialShapeDrawableBackground(@NonNull Drawable background) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        if (background instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(
                    ColorStateList.valueOf(((ColorDrawable) background).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(getContext());
        return materialShapeDrawable;
    }

    private int toPx(float dip) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dip * density +0.5f);
    }
}
