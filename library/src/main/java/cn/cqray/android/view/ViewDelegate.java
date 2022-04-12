package cn.cqray.android.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import cn.cqray.android.R;
import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.app.SupportDelegate;
import cn.cqray.android.app.SupportProvider;
import cn.cqray.android.app.SupportActivity;
import cn.cqray.android.app.SupportFragment;
import cn.cqray.android.exception.ExceptionDispatcher;
import cn.cqray.android.exception.ExceptionType;
import cn.cqray.android.state.BusyDialog;
import cn.cqray.android.state.StateRefreshLayout;
import cn.cqray.android.state.ViewState;
import cn.cqray.android.strategy.ToolbarStrategy;
import cn.cqray.android.util.ButterKnifeUtils;
import cn.cqray.android.util.ObjectUtils;
import cn.cqray.android.widget.Toolbar;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 界面布局代理
 * @author Cqray
 */
@Accessors(prefix = "m")
public final class ViewDelegate {

    /** 内容控件 **/
    private @Getter View mContentView;
    /** 标题 **/
    private @Getter Toolbar mToolbar;
    /** 刷新控件 **/
    private @Getter StateRefreshLayout mRefreshLayout;
    /** 头部容器 **/
    private FrameLayout mHeaderLayout;
    /** 底部容器 **/
    private FrameLayout mFooterLayout;
    /** Activity内容布局（android.R.id.content） **/
    private ViewGroup mActivityContent;
    /** ButterKnife绑定 **/
    private Object mUnBinder;
    /** 忙碌对话框 **/
    private BusyDialog mBusyDialog;
    /** Fragment、Activity背景 **/
    private MutableLiveData<Drawable> mBackground;
    /** 生命周期管理 **/
    private final LifecycleOwner mLifecycleOwner;
    /** 生命周期监听 **/
    private final LifecycleEventObserver mEventObserver = (source, event) -> {
        if (event == Lifecycle.Event.ON_DESTROY) {
            onDestroy();
        }
    };

    public ViewDelegate(AppCompatActivity activity) {
        mLifecycleOwner = activity;
        mLifecycleOwner.getLifecycle().addObserver(mEventObserver);
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        setBackground(strategy.getActivityBackground());
    }

    public ViewDelegate(Fragment fragment) {
        mLifecycleOwner = fragment;
        mLifecycleOwner.getLifecycle().addObserver(mEventObserver);
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
        mContentView = inflate(R.layout.starter_default_layout);
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
        mToolbar = view.findViewById(R.id.starter_toolbar);
        mHeaderLayout = view.findViewById(R.id.starter_header_layout);
        mFooterLayout = view.findViewById(R.id.starter_footer_layout);
        mRefreshLayout = view.findViewById(R.id.starter_refresh_layout);
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
                if (mContentView != null) {
                    mContentView.setBackground(drawable);
                }
            });
        }
        mBackground.setValue(background);
    }

    public void setIdle() {
        setState(ViewState.IDLE, null);
    }

    public void setBusy(String text) {
        setState(ViewState.BUSY, text);
    }

    public void setEmpty(String text) {
        setState(ViewState.EMPTY, text);
    }

    public void setError(String text) {
        setState(ViewState.ERROR, text);
    }

    public void setState(ViewState state, String text) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setState(state, text);
        } else {
            if (state == ViewState.BUSY && mBusyDialog == null) {
                mBusyDialog = new BusyDialog();
                if (mLifecycleOwner instanceof FragmentActivity) {
                    mBusyDialog.show(((FragmentActivity) mLifecycleOwner).getSupportFragmentManager(), null);
                } else if (mLifecycleOwner instanceof Fragment) {
                    mBusyDialog.show(((Fragment) mLifecycleOwner).getChildFragmentManager(), null);
                }
            } else if (mBusyDialog != null) {
                mBusyDialog.dismiss();
                mBusyDialog = null;
            }
        }
    }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     */
    public <T extends View> T findViewById(@IdRes int resId) {
        return isContentViewExist() ? mContentView.findViewById(resId) : null;
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
        System.gc();
    }

    /**
     * 设置Activity布局内容
     */
    void setActivityContentView() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            ((AppCompatActivity) mLifecycleOwner).getDelegate().setContentView(mContentView);
        }
    }

    /**
     * 通过ButterKnife绑定界面
     */
    void initUnBinder() {
        if (isContentViewExist()) {
            ButterKnifeUtils.unbind(mUnBinder);
            mUnBinder = ButterKnifeUtils.bind(mLifecycleOwner, mContentView);
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

        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        // 设置StateRefreshLayout相应状态的适配器
        if (mRefreshLayout != null) {
            mRefreshLayout.setBusyAdapter(ObjectUtils.deepClone(strategy.getBusyAdapter()));
            mRefreshLayout.setEmptyAdapter(ObjectUtils.deepClone(strategy.getEmptyAdapter()));
            mRefreshLayout.setErrorAdapter(ObjectUtils.deepClone(strategy.getErrorAdapter()));
        }

        initToolbar();
        initUnBinder();
    }

    void initToolbar() {
        // 初始化标题栏监听事件
        if (mToolbar != null && mLifecycleOwner instanceof SupportProvider) {
            mToolbar.setBackListener(v -> {
                SupportDelegate delegate = ((SupportProvider) mLifecycleOwner).getStarterDelegate();
                delegate.pop();
            });
        }
        ToolbarStrategy strategy = Starter.getInstance().getToolbarStrategy();
        if (mToolbar != null) {
            mToolbar.setUseRipple(strategy.isUserRipple()).setBackground(strategy.getBackground());
            // 设置标题栏标题属性
            mToolbar.setTitleCenter(strategy.isTitleCenter())
                    .setTitleTextColor(strategy.getTitleTextColor())
                    .setTitleTextSize(strategy.getTitleTextSize(), TypedValue.COMPLEX_UNIT_PX)
                    .setTitleTypeface(strategy.getTitleTypeface())
                    .setTitleSpace(strategy.getTitleSpace(), TypedValue.COMPLEX_UNIT_PX);
            // 设置标题栏返回控件属性
            mToolbar.setBackText(strategy.getBackText())
                    .setBackIcon(strategy.getBackIcon())
                    .setBackTextColor(strategy.getBackTextColor())
                    .setBackTextSize(strategy.getBackTextSize())
                    .setBackTypeface(strategy.getBackTypeface());
            if (strategy.getBackIconTintColor() != null) {
                mToolbar.setBackIconTintColor(strategy.getBackIconTintColor());
            }
            // 设置标题栏Action控件属性
            mToolbar.setDefaultActionTextColor(strategy.getActionTextColor())
                    .setDefaultActionTextSize(strategy.getActionTextSize(), TypedValue.COMPLEX_UNIT_PX)
                    .setDefaultActionTypeface(strategy.getActionTypeface());
            // 设置标题栏分割线属性
            mToolbar.setDividerColor(strategy.getDividerColor())
                    .setDividerHeight(strategy.getDividerHeight(), TypedValue.COMPLEX_UNIT_PX)
                    .setDividerMargin(strategy.getDividerMargin(), TypedValue.COMPLEX_UNIT_PX)
                    .setDividerVisible(strategy.isDividerVisible());
        }
    }

    /**
     * 渲染界面
     * @param resId 界面ID
     */
    View inflate(@LayoutRes int resId) {
        FragmentActivity activity;
        // 获取Activity
        if (mLifecycleOwner instanceof AppCompatActivity) {
            activity = (AppCompatActivity) mLifecycleOwner;
        } else {
            activity = ((Fragment) mLifecycleOwner).requireActivity();
        }
        // 缓存控件
        if (mActivityContent == null) {
            mActivityContent = activity.findViewById(android.R.id.content);
        }
        return LayoutInflater.from(activity).inflate(resId, mActivityContent, false);
    }

    /**
     * ContentView是否存在
     */
    boolean isContentViewExist() {
        if (mContentView == null) {
            ExceptionDispatcher.dispatchThrowable(mLifecycleOwner, ExceptionType.CONTENT_VIEW_NULL);
            return false;
        }
        return true;
    }

}
