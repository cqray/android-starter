package cn.cqray.android.view;

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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import cn.cqray.android.R;
import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.app.StarterDelegate;
import cn.cqray.android.app.StarterProvider;
import cn.cqray.android.app.SupportActivity;
import cn.cqray.android.app.SupportFragment;
import cn.cqray.android.exception.ExceptionManager;
import cn.cqray.android.exception.ViewException;
import cn.cqray.android.state.BusyDialog;
import cn.cqray.android.state.StateRefreshLayout;
import cn.cqray.android.state.ViewState;
import cn.cqray.android.util.ButterKnifeUtils;
import cn.cqray.android.util.ObjectUtils;
import cn.cqray.android.widget.Toolbar;

/**
 * 界面布局代理
 * @author Cqray
 */
public final class ViewDelegate {

    /** 内容控件 **/
    private View mContentView;
    /** 标题 **/
    private Toolbar mToolbar;
    /** 刷新控件 **/
    private StateRefreshLayout mRefreshLayout;
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
            ExceptionManager.getInstance().showException(new ViewException("不支持设置Header。"));
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
            ExceptionManager.getInstance().showException(new ViewException("不支持设置Header。"));
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
            ExceptionManager.getInstance().showException(new ViewException("不支持设置Footer。"));
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
            ExceptionManager.getInstance().showException(new ViewException("不支持设置Footer。"));
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

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public StateRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public View getContentView() {
        return mContentView;
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
//        SupportHandler<CommonToolbar> tHandler = AndroidLibrary.getInstance().getToolbarHandler();
//        SupportHandler<StateRefreshLayout> rHandler = AndroidLibrary.getInstance().getRefreshLayoutHandler();
//        // 全局初始化Toolbar
//        if (tHandler != null && mToolbar != null) {
//            tHandler.onHandle(mDelegateProvider, mToolbar);
//        }
//        // 全局初始化刷新控件
//        if (rHandler != null && mRefreshLayout != null) {
//            rHandler.onHandle(mDelegateProvider, mRefreshLayout);
//        }
        // 初始化ButterKnife
        initUnBinder();
    }

    void initToolbar() {
        // 初始化标题栏监听事件
        if (mToolbar != null && mLifecycleOwner instanceof StarterProvider) {
            mToolbar.setBackListener(v -> {
                StarterDelegate delegate = ((StarterProvider) mLifecycleOwner).getStarterDelegate();
                if (!delegate.pop()) {
                    delegate.popParent();
                }
            });
        }
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        if (mToolbar != null) {
            //===========标题部分===========//
            // 设置标题背景
            if (strategy.getToolbarBackground() != null) {
                mToolbar.setBackground(strategy.getToolbarBackground());
            }
            // 设置标题文字颜色
            if (strategy.getToolbarTitleTextColor() != Integer.MIN_VALUE) {
                mToolbar.setTitleTextColor(strategy.getToolbarTitleTextColor());
            }
            // 设置标题文字大小
            if (strategy.getToolbarTitleTextSize() != Integer.MIN_VALUE) {
                mToolbar.setTitleTextSize(strategy.getToolbarTitleTextSize());
            }
            // 设置标题文字样式
            if (strategy.getToolbarTitleTypeface() != null) {
                mToolbar.setTitleTypeface(strategy.getToolbarTitleTypeface());
            }
            // 设置标题左右间隔
            if (strategy.getToolbarTitleSpace() != Integer.MIN_VALUE) {
                mToolbar.setTitleSpace(strategy.getToolbarTitleSpace());
            }
            //===========返回部分===========//
            // 设置图标
            if (strategy.getToolbarBackIcon() != null) {
                mToolbar.setBackIcon(strategy.getToolbarBackIcon());
            }
            // 设置图标颜色
            if (strategy.getToolbarBackIconTintColor() != Integer.MIN_VALUE) {
                mToolbar.setBackIconTintColor(strategy.getToolbarBackIconTintColor());
            }
            // 设置标题返回文字颜色
            if (strategy.getToolbarBackTextColor() != Integer.MIN_VALUE) {
                mToolbar.setBackTextColor(strategy.getToolbarBackTextColor());
            }
            // 设置标题返回文字大小
            if (strategy.getToolbarBackTextSize() != Integer.MIN_VALUE) {
                mToolbar.setBackTextSize(strategy.getToolbarBackTextSize());
            }
            // 设置标题返回文字样式
            if (strategy.getToolbarBackTypeface() != null) {
                mToolbar.setBackTypeface(strategy.getToolbarBackTypeface());
            }
            //===========Action部分===========//
            // 设置标题Action文字颜色
            if (strategy.getToolbarActionTextColor() != Integer.MIN_VALUE) {
                mToolbar.setActionTextColor(strategy.getToolbarActionTextColor());
            }
            // 设置标题Action文字大小
            if (strategy.getToolbarActionTextSize() != Integer.MIN_VALUE) {
                mToolbar.setActionTextSize(strategy.getToolbarActionTextSize());
            }
            // 设置标题Action文字样式
            if (strategy.getToolbarActionTypeface() != null) {
                mToolbar.setActionTypeface(strategy.getToolbarActionTypeface());
            }
            //===========分割线部分===========//
            // 设置分割线颜色
            if (strategy.getToolbarDividerColor() != Integer.MIN_VALUE) {
                mToolbar.setDividerColor(strategy.getToolbarDividerColor());
            }
            // 设置分割线颜色
            if (strategy.getToolbarDividerHeight() != Integer.MIN_VALUE) {
                mToolbar.setDividerHeight(strategy.getToolbarDividerHeight());
            }
            // 设置分割线颜色
            if (strategy.getToolbarDividerMargin() != Integer.MIN_VALUE) {
                mToolbar.setDividerMargin(strategy.getToolbarDividerMargin());
            }
            // 其他属性
            mToolbar.setTitleCenter(strategy.isToolbarTitleCenter())
                    .setUseRipple(strategy.isToolbarUserRipple())
                    .setBackText(strategy.getToolbarBackText())
                    .setDividerVisible(strategy.isToolbarDividerVisible());
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
            ExceptionManager.getInstance().showException(null);
            return false;
        }
        return true;
    }


}