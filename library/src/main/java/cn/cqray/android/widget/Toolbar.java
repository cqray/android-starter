package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


    private TextView mTitleView;
    private View mDivider;
    private ImageView mNavIcon;
    private TextView mNavText;
    private Space mNavSpace;
    private LinearLayout mNavLayout;
    private LinearLayout mActionLayout;

    /** 阴影高度 **/
    private float mElevation;
    private SparseArray<View> mViewArray;
    private SparseBooleanArray mVisibleArray;


    private LifecycleRegistry mLifecycleRegistry;
    private LifecycleOwner mLifecycleOwener;

    private final MutableLiveData<Boolean> mRippleEnable = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> mTitleCenter = new MutableLiveData<>(false);

    public Toolbar(@NonNull Context context) {
        this(context, null);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 设置MaterialShapeDrawable
        if (getBackground() == null || getBackground() instanceof ColorDrawable) {
            ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(context));
        }
        // 设置属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        mElevation = ta.getDimension(R.styleable.Toolbar_tbElevation, 0);
        ta.recycle();

        setElevation(mElevation);

        initToolbar(context, attrs);
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


    private void initToolbar(Context context, AttributeSet attrs) {
        mLifecycleOwener = () -> mLifecycleRegistry;
        mLifecycleRegistry = new LifecycleRegistry(mLifecycleOwener);
        mViewArray = new SparseArray<>();
        mVisibleArray = new SparseBooleanArray();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.INITIALIZED);
        // 设置界面
        LayoutInflater.from(context).inflate(R.layout.starter_toolbar_layout2, this);
        mNavLayout = findViewById(R.id.starter_toolbar_nav_layout);
        mNavIcon = (ImageView) mNavLayout.getChildAt(0);
        mNavSpace = (Space) mNavLayout.getChildAt(1);
        mNavText = (TextView) mNavLayout.getChildAt(2);
        mActionLayout = findViewById(R.id.starter_toolbar_action_layout);
        mDivider = findViewById(R.id.starter_toolbar_divider);


        mRippleEnable.observe(mLifecycleOwener, aBoolean -> {

        });
        mTitleCenter.observe(mLifecycleOwener, aBoolean -> {

        });

    }


    @NonNull
    private MaterialShapeDrawable createMaterialShapeDrawableBackground(@NonNull Context context) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        Drawable originalBackground = getBackground();
        if (originalBackground instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(
                    ColorStateList.valueOf(((ColorDrawable) originalBackground).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(context);
        return materialShapeDrawable;
    }

    @Override
    public void setElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(elevation);
        }
        MaterialShapeUtils.setElevation(this, elevation);
    }

    @Override
    public float getElevation() {
        return mElevation;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {}

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {}
}
