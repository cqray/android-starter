package cn.cqray.android.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ActivityUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import cn.cqray.android.R;
import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.exception.ExceptionDispatcher;
import cn.cqray.android.exception.ExceptionType;
import cn.cqray.android.state.StateDelegate;
import cn.cqray.android.state.ViewState;
import cn.cqray.android.util.ButterKnifeUtils;
import cn.cqray.android.widget.Toolbar;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 界面布局代理
 * @author Cqray
 */
@Accessors(prefix = "m")
public final class ViewDelegate {

    /** 根控件 **/
    private @Getter View mRootView;
    /** 内容控件 **/
    private @Getter View mContentView;
    /** 标题 **/
    private @Getter Toolbar mToolbar;
    /** 刷新控件 **/
    private @Getter SmartRefreshLayout mRefreshLayout;
    /** 头部容器 **/
    private @Getter FrameLayout mHeaderLayout;
    /** 底部容器 **/
    private @Getter FrameLayout mFooterLayout;
    /** Activity内容布局（android.R.id.content） **/
    private ViewGroup mActivityContent;
    /** ButterKnife绑定 **/
    private Object mUnBinder;
    /** Fragment、Activity背景 **/
    private MutableLiveData<Drawable> mBackground;
    /** 生命周期管理 **/
    private @Getter final LifecycleOwner mLifecycleOwner;
    /** 状态委托 **/
    private @Getter final StateDelegate mStateDelegate;
    /** 生命周期监听 **/
    private final LifecycleEventObserver mEventObserver = (source, event) -> {
        if (event == Lifecycle.Event.ON_DESTROY) {
            onDestroy();
        }
    };

    public ViewDelegate(AppCompatActivity activity) {
        mLifecycleOwner = activity;
        mLifecycleOwner.getLifecycle().addObserver(mEventObserver);
        mStateDelegate = new StateDelegate(activity);
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        if (strategy.getActivityBackground() != null) {
            setBackground(strategy.getActivityBackground());
        }
    }

    public ViewDelegate(Fragment fragment) {
        mLifecycleOwner = fragment;
        mLifecycleOwner.getLifecycle().addObserver(mEventObserver);
        mStateDelegate = new StateDelegate(fragment);
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        setBackground(strategy.getFragmentBackground());
    }

    /**
     * 设置默认布局
     * @param id 布局Id
     */
    public void setContentView(@LayoutRes int id) {
        setContentView(inflate(id));
    }

    /**
     * 设置默认布局
     * @param view 布局
     */
    public void setContentView(View view) {
        mContentView = view;
        mRootView = inflate(R.layout.starter_layout_default);
        mToolbar = findViewById(R.id.starter_toolbar);
        mHeaderLayout = findViewById(R.id.starter_header_layout);
        mFooterLayout = findViewById(R.id.starter_footer_layout);
        mRefreshLayout = findViewById(R.id.starter_refresh_layout);
        assert mRefreshLayout != null;
        mRefreshLayout.addView(view);
        setActivityContentView();
        initSupportView();
    }

    /**
     * 设置原生布局
     * @param id 布局Id
     */
    public void setNativeContentView(@LayoutRes int id) {
        setNativeContentView(inflate(id));
    }

    /**
     * 设置原生布局
     * @param view 布局
     */
    public void setNativeContentView(@NonNull View view) {
        mContentView = view;
        mRootView = inflate(R.layout.starter_layout_native);
        mToolbar = view.findViewById(R.id.starter_toolbar);
        mHeaderLayout = view.findViewById(R.id.starter_header_layout);
        mFooterLayout = view.findViewById(R.id.starter_footer_layout);
        mRefreshLayout = view.findViewById(R.id.starter_refresh_layout);
        ((FrameLayout) mRootView).addView(view);
        setActivityContentView();
        initSupportView();
    }

    /**
     * 设置头部布局
     * @param id 布局ID
     */
    public void setHeaderView(@LayoutRes int id) {
        setHeaderView(inflate(id));
    }

    /**
     * 设置头部布局
     * @param view 布局
     */
    public void setHeaderView(View view) {
        if (mHeaderLayout == null) {
            ExceptionDispatcher.dispatchThrowable(mLifecycleOwner, ExceptionType.HEADER_UNSUPPORTED);
        } else {
            mHeaderLayout.removeAllViews();
            mHeaderLayout.addView(view);
            initUnBinder();
        }
    }

    /**
     * 设置头部布局悬浮
     * @param floating 是否悬浮
     */
    public void setHeaderFloating(boolean floating) {
        if (mHeaderLayout == null) {
            ExceptionDispatcher.dispatchThrowable(mLifecycleOwner, ExceptionType.HEADER_UNSUPPORTED);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRefreshLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, floating ? R.id.toolbar : R.id.header_layout);
            mRefreshLayout.requestLayout();
        }
    }

    /**
     * 设置底部布局
     * @param id 布局ID
     */
    public void setFooterView(@LayoutRes int id) {
        setFooterView(inflate(id));
    }

    /**
     * 设置底部布局
     * @param view 布局
     */
    public void setFooterView(View view) {
        if (mFooterLayout == null) {
            ExceptionDispatcher.dispatchThrowable(mLifecycleOwner, ExceptionType.FOOTER_UNSUPPORTED);
        } else {
            mFooterLayout.removeAllViews();
            mFooterLayout.addView(view);
            initUnBinder();
        }
    }

    /**
     * 设置底部布局悬浮
     * @param floating 是否悬浮
     */
    public void setFooterFloating(boolean floating) {
        if (mFooterLayout == null) {
            ExceptionDispatcher.dispatchThrowable(mLifecycleOwner, ExceptionType.FOOTER_UNSUPPORTED);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRefreshLayout.getLayoutParams();
            params.addRule(RelativeLayout.ABOVE, floating ? 0 : R.id.footer_layout);
            mRefreshLayout.requestLayout();
        }
    }

    /**
     * 设置背景
     * @param resId 背景资源Id
     */
    public void setBackgroundRes(@DrawableRes int resId) {
        Drawable background = ContextCompat.getDrawable(getContext(), resId);
        setBackground(background);
    }

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    public void setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
    }

    /**
     * 设置背景
     * @param background 背景
     */
    public synchronized void setBackground(Drawable background) {
        if (mBackground == null) {
            mBackground = new MutableLiveData<>();
            mBackground.observe(mLifecycleOwner, drawable -> {
                if (mLifecycleOwner instanceof AppCompatActivity) {
                    AppCompatActivity act = (AppCompatActivity) mLifecycleOwner;
                    boolean isTranslucentOrFloating = act.getIntent().getBooleanExtra("activity:isTranslucentOrFloating", false);
                    if (isTranslucentOrFloating && mRootView != null) {
                        mRootView.setBackground(drawable);
                    } else if (!isTranslucentOrFloating) {
                        act.getWindow().setBackgroundDrawable(drawable);
                    }
                } else if (mRootView != null) {
                    mRootView.setBackground(drawable);
                }
            });
        }
        mBackground.setValue(background);
    }

    public void setIdle() {
        mStateDelegate.setIdle();
    }

    public void setBusy(String... texts) {
        mStateDelegate.setBusy(texts);
    }

    public void setEmpty(String... texts) {
        mStateDelegate.setEmpty(texts);
    }

    public void setError(String... texts) {
        mStateDelegate.setError(texts);
    }

    public void setState(ViewState state, String text) {
        mStateDelegate.setState(state, text);
    }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     */
    public <T extends View> T findViewById(@IdRes int resId) {
        return isRootViewExist() ? mRootView.findViewById(resId) : null;
    }

    public Context getContext() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return (Context) mLifecycleOwner;
        } else {
            return ((Fragment) mLifecycleOwner).requireContext();
        }
    }

    /**
     * 销毁界面
     */
    void onDestroy() {
        ButterKnifeUtils.unbind(mUnBinder);
        mActivityContent = null;
        mRefreshLayout = null;
        mHeaderLayout = null;
        mFooterLayout = null;
        mToolbar = null;
        mContentView = null;
        mRootView = null;
        System.gc();
    }

    /**
     * 设置Activity布局内容
     */
    void setActivityContentView() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            ((AppCompatActivity) mLifecycleOwner).getDelegate().setContentView(mRootView);
        }
    }

    /**
     * 通过ButterKnife绑定界面
     */
    void initUnBinder() {
        if (isRootViewExist()) {
            ButterKnifeUtils.unbind(mUnBinder);
            mUnBinder = ButterKnifeUtils.bind(mLifecycleOwner, mRootView);
        }
    }

    /**
     * 初始化界面相关控件
     */
    void initSupportView() {
        if (mLifecycleOwner instanceof SupportActivity) {
            ((SupportActivity) mLifecycleOwner).mToolbar = mToolbar;
            ((SupportActivity) mLifecycleOwner).mContentView = mContentView;
            ((SupportActivity) mLifecycleOwner).mRefreshLayout = mRefreshLayout;
        } else if (mLifecycleOwner instanceof SupportFragment) {
            ((SupportFragment) mLifecycleOwner).mToolbar = mToolbar;
            ((SupportFragment) mLifecycleOwner).mContentView = mContentView;
            ((SupportFragment) mLifecycleOwner).mRefreshLayout = mRefreshLayout;
        }
        if (mRefreshLayout != null) {
            mStateDelegate.attachLayout(mRefreshLayout);
        } else if (mRootView instanceof FrameLayout) {
            mStateDelegate.attachLayout((FrameLayout) mRootView);
        }

        // 初始化标题
        initToolbar();
        // 初始化ButterKnife
        initUnBinder();
    }

    void initToolbar() {
        // 初始化标题栏监听事件
        if (mToolbar != null && mLifecycleOwner instanceof SupportProvider) {
            mToolbar.setBackListener(v -> {
                SupportDelegate delegate = ((SupportProvider) mLifecycleOwner).getSupportDelegate();
                delegate.pop();
            });
        }
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        if (mToolbar != null) {
            mToolbar.setUseRipple(strategy.isToolbarUserRipple())
                    .setBackground(strategy.getToolbarBackground());
            // 设置标题栏标题属性
            mToolbar.setTitleCenter(strategy.isToolbarTitleCenter())
                    .setTitleTextColor(strategy.getToolbarTitleTextColor())
                    .setTitleTextSize(strategy.getToolbarTitleTextSize())
                    .setTitleTypeface(strategy.getToolbarTitleTypeface())
                    .setTitleSpace(strategy.getToolbarTitleSpace());
            // 设置标题栏返回控件属性
            mToolbar.setBackText(strategy.getToolbarBackText())
                    .setBackIcon(strategy.getToolbarBackIcon())
                    .setBackTextColor(strategy.getToolbarBackTextColor())
                    .setBackTextSize(strategy.getToolbarBackTextSize())
                    .setBackTypeface(strategy.getToolbarBackTypeface());
            if (strategy.getToolbarBackIconTintColor() != null) {
                mToolbar.setBackIconTintColor(strategy.getToolbarBackIconTintColor());
            }
            // 设置标题栏Action控件属性
            mToolbar.setDefaultActionTextColor(strategy.getToolbarActionTextColor())
                    .setDefaultActionTextSize(strategy.getToolbarActionTextSize())
                    .setDefaultActionTypeface(strategy.getToolbarActionTypeface());
            // 设置标题栏分割线属性
            mToolbar.setDividerColor(strategy.getToolbarDividerColor())
                    .setDividerHeight(strategy.getToolbarDividerHeight())
                    .setDividerMargin(strategy.getToolbarDividerMargin())
                    .setDividerVisible(strategy.isToolbarDividerVisible());
        }
    }

    /**
     * 渲染界面
     * @param resId 界面ID
     */
    View inflate(@LayoutRes int resId) {
        // 获取Activity
        Activity activity = ActivityUtils.getTopActivity();
        // 缓存控件
        if (mActivityContent == null) {
            mActivityContent = activity.findViewById(android.R.id.content);
        }
        return LayoutInflater.from(activity).inflate(resId, mActivityContent, false);
    }

    /**
     * RootView是否存在
     */
    boolean isRootViewExist() {
        if (mRootView == null) {
            ExceptionDispatcher.dispatchThrowable(mLifecycleOwner, ExceptionType.CONTENT_VIEW_NULL);
            return false;
        }
        return true;
    }

}
