package cn.cqray.android.swipe;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import cn.cqray.android.app.StarterProvider;
import cn.cqray.android.util.CheckUtils;
import io.reactivex.functions.Consumer;

/**
 * 滑动回退委托
 * @author Cqray
 */
public final class SwipeBackDelegate {
    /** 边缘生效百分比 **/
    private static final float SWIPE_EDGE_PERCENT = 0.2f;
    private SwipeBackLayout mSwipeLayout;
    //private DisposablePool mDisposablePool;
    private final StarterProvider mDelegateProvider;

    public SwipeBackDelegate(@NonNull StarterProvider provider) {
        CheckUtils.checkDelegateProvider(provider);
        mDelegateProvider = provider;
        LifecycleOwner owner = (LifecycleOwner) provider;
        // 销毁监听
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                SwipeBackHelper.removeSwipeDelegate(SwipeBackDelegate.this);
            }
        });
        SwipeBackHelper.addSwipeDelegate(this);
    }

    public void onAttachActivity() {
//        if (mDelegateProvider.onSwipeBackSupport()
//                && mDelegateProvider instanceof Activity) {
//            AppCompatActivity act = (AppCompatActivity) mDelegateProvider;
//            Window window = act.getWindow();
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
//            mSwipeLayout = new SwipeBackLayout(act);
//            mSwipeLayout.setLayoutParams(params);
//            mSwipeLayout.setSwipeEdgePercent(SWIPE_EDGE_PERCENT);
//            mSwipeLayout.attachToActivity(mDelegateProvider);
//        }
    }

    public View onAttachFragment(View view) {
//        if (mDelegateProvider.onSwipeBackSupport()
//                && mDelegateProvider instanceof Fragment
//                && view != null ) {
//            mSwipeLayout = new SwipeBackLayout(((Fragment) mDelegateProvider).requireContext());
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
//            mSwipeLayout.setLayoutParams(params);
//            mSwipeLayout.setBackgroundColor(Color.TRANSPARENT);
//            mSwipeLayout.setSwipeEdgePercent(SWIPE_EDGE_PERCENT);
//            mSwipeLayout.attachToFragment(mDelegateProvider, view);
//            return mSwipeLayout;
//        }
        return view;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeLayout;
    }

    /**
     * 设置能否滑动
     * @param enable 能否滑动
     */
    public SwipeBackDelegate swipeBackEnable(final boolean enable) {
        timer(aLong -> mSwipeLayout.setSwipeBackEnable(enable));
        return this;
    }

    /**
     * 滑动中，上一个页面View的遮罩透明度
     * @param alpha 遮罩透明度
     */
    public SwipeBackDelegate scrimAlpha(@FloatRange(from = 0.0f, to = 1.0f) final float alpha) {
        timer(aLong -> mSwipeLayout.setScrimAlpha(alpha));
        return this;
    }

    /**
     * 滑动中，上一个页面View的遮罩颜色
     * @param color 颜色
     */
    public SwipeBackDelegate scrimColor(final int color) {
        timer(aLong -> mSwipeLayout.setScrimColor(color));
        return this;
    }

    /**
     * 可以启动滑动的范围
     * @param widthPixel 范围值
     */
    public SwipeBackDelegate swipeEdge(final int widthPixel) {
        timer(aLong -> mSwipeLayout.setSwipeEdge(widthPixel));
        return this;
    }

    /**
     * 可以启动滑动的范围百分比
     * @param percent 范围值百分比
     */
    public SwipeBackDelegate swipeEdgePercent(@FloatRange(from = 0.0f, to = 1.0f) final float percent) {
        timer(aLong -> mSwipeLayout.setSwipeEdgePercent(percent));
        return this;
    }

    /**
     * 滑动方向
     * @param orientation 滑动方向
     */
    public SwipeBackDelegate edgeOrientation(@NonNull final EdgeOrientation orientation) {
        timer(aLong -> mSwipeLayout.setEdgeOrientation(orientation));
        return this;
    }

    /**
     * 设置阴影Drawable
     * @param shadow 阴影
     * @param orientation 方向
     */
    public SwipeBackDelegate shadow(final Drawable shadow, final EdgeOrientation orientation) {
        timer(aLong -> mSwipeLayout.setShadow(shadow, orientation));
        return this;
    }

    /**
     * 设置阴影资源
     * @param resId 阴影资源
     * @param orientation 方向
     */
    public SwipeBackDelegate shadow(final int resId, final EdgeOrientation orientation) {
        timer(aLong -> mSwipeLayout.setShadow(resId, orientation));
        return this;
    }

    /**
     * 设置滑动时上一个界面的偏移百分比
     * @param percent 偏移百分比
     */
    public SwipeBackDelegate offsetPercent(final float percent) {
        timer(aLong -> mSwipeLayout.setOffsetPercent(percent));
        return this;
    }

    /**
     * 设置滑动到多少百分比后松手会关闭界面
     * @param percent 百分比
     */
    public SwipeBackDelegate closePercent(final float percent) {
        timer(aLong -> mSwipeLayout.setClosePercent(percent));
        return this;
    }

    /**
     * 监听事件
     * @param listener 监听事件
     */
    public SwipeBackDelegate addOnSwipeListener(final SwipeBackLayout.OnSwipeListener listener) {
        timer(aLong -> mSwipeLayout.addOnSwipeListener(listener));
        return this;
    }

    public StarterProvider getSupportDelegateProvider() {
        return mDelegateProvider;
    }

//    private DisposablePool getDisposablePool() {
//        if (mDisposablePool == null) {
//            synchronized (mDelegateProvider) {
//                if (mDisposablePool == null) {
//                    mDisposablePool = new DisposablePool((LifecycleOwner) mDelegateProvider);
//                }
//            }
//        }
//        return mDisposablePool;
//    }
//
    private void timer(Consumer<Long> consumer) {
//        if (!mDelegateProvider.onSwipeBackSupport()) {
//            return;
//        }
//        getDisposablePool().timer(consumer, 0);
    }
}
