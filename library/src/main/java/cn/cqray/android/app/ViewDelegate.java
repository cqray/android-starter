package cn.cqray.android.app;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import cn.cqray.android.R;
import cn.cqray.android.exception.ExceptionManager;
import cn.cqray.android.exception.ViewException;
import cn.cqray.android.state.StateRefreshLayout;
import cn.cqray.android.util.ButterKnifeUtils;
import cn.cqray.android.widget.CommonToolbar;

/**
 * 界面布局代理
 * @author Cqray
 */
public final class ViewDelegate {

    /** 内容控件 **/
    private View mContentView;
    /** 标题 **/
    private CommonToolbar mToolbar;
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
    }

    public ViewDelegate(Fragment fragment) {
        mLifecycleOwner = fragment;
        mLifecycleOwner.getLifecycle().addObserver(mEventObserver);
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
        mContentView = inflate(R.layout.starter_layout_default);
        mToolbar = findViewById(R.id.toolbar);
        mHeaderLayout = findViewById(R.id.header_layout);
        mFooterLayout = findViewById(R.id.footer_layout);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        assert mRefreshLayout != null;
        mRefreshLayout.addView(view);
        setActivityContentView();
        initSupportView();
    }

    public void setNormalContentView(@LayoutRes int id) {
        setNormalContentView(inflate(id));
    }

    public void setNormalContentView(View view) {
        mContentView = inflate(R.layout.starter_layout_normal);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        ((ViewGroup) mContentView).addView(view);
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
    public void setNativeContentView(View view) {
        mContentView = view;
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
    public void setBackground(Drawable background) {
        if (mBackground == null) {
            mBackground = new MutableLiveData<>();
            mBackground.observe(mLifecycleOwner, drawable -> mContentView.setBackground(drawable));
        }
        mBackground.setValue(background);
    }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     */
    public <T extends View> T findViewById(@IdRes int resId) {
        return isContentViewExist() ? mContentView.findViewById(resId) : null;
    }

    public CommonToolbar getToolbar() {
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

    void initSupportView() {
//        final SupportDelegate delegate = mDelegateProvider.getSupportDelegate();
//        if (mDelegateProvider instanceof AppCompatActivity) {
//            if (mDelegateProvider instanceof SupportActivity) {
//                ((SupportActivity) mDelegateProvider).mToolbar = mToolbar;
//                ((SupportActivity) mDelegateProvider).mContentView = mContentView;
//                ((SupportActivity) mDelegateProvider).mRefreshLayout = mRefreshLayout;
//            }
//        } else {
//            if (mDelegateProvider instanceof SupportFragment) {
//                ((SupportFragment) mDelegateProvider).mToolbar = mToolbar;
//                ((SupportFragment) mDelegateProvider).mContentView = mContentView;
//                ((SupportFragment) mDelegateProvider).mRefreshLayout = mRefreshLayout;
//            }
//        }
//        // 初始化标题栏监听事件
//        if (mToolbar != null) {
//            mToolbar.setNavListener(v -> delegate.pop());
//        }
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
