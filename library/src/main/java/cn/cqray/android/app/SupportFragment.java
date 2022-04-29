package cn.cqray.android.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import cn.cqray.android.state.StateRefreshLayout;
import cn.cqray.android.state.ViewState;
import cn.cqray.android.tip.TipDelegate;
import cn.cqray.android.tip.TipProvider;
import cn.cqray.android.view.ViewDelegate;
import cn.cqray.android.view.ViewProvider;
import cn.cqray.android.widget.Toolbar;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 基础Fragment
 * @author Cqray
 */
public class SupportFragment extends Fragment implements ViewProvider, SupportProvider, TipProvider {

    /** 设置的布局 **/
    public View mContentView;
    /** 标题 **/
    public Toolbar mToolbar;
    /** 状态刷新控件 **/
    public SmartRefreshLayout mRefreshLayout;

    private final ObservableDelegate mObservableDelegate = new ObservableDelegate(this);

    private final TipDelegate mTipDelegate = new TipDelegate(this);
    /** 布局代理 **/
    private final ViewDelegate mViewDelegate = new ViewDelegate(this);
    /** 启动代理 **/
    private final SupportDelegate mSupportDelegate = SupportDelegate.get(this);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreating(savedInstanceState);
        View view = mViewDelegate.getContentView() == null
                ? super.onCreateView(inflater, container, savedInstanceState)
                : mViewDelegate.getContentView();
        return view;
        //return mDelegate.getSwipeDelegate().onAttachFragment(view);
    }

    public void onCreating(@Nullable Bundle savedInstanceState) {}

    @Override
    public ViewDelegate getViewDelegate() {
        return mViewDelegate;
    }

    public <T extends View> T findViewById(@IdRes int resId) {
        return mViewDelegate.findViewById(resId);
    }

    public void setContentView(View view) {
        mViewDelegate.setContentView(view);
    }

    public void setContentView(int layoutResId) {
        mViewDelegate.setContentView(layoutResId);
    }

    public void setNativeContentView(View view) {
        mViewDelegate.setNativeContentView(view);
    }

    public void setNativeContentView(int layoutResId) {
        mViewDelegate.setNativeContentView(layoutResId);
    }

    public void setHeaderView(int layoutResId) {
        mViewDelegate.setHeaderView(layoutResId);
    }

    public void setHeaderView(View view) {
        mViewDelegate.setHeaderView(view);
    }

    public void setHeaderFloating(boolean floating) {
        mViewDelegate.setHeaderFloating(floating);
    }

    public void setFooterView(int layoutResId) {
        mViewDelegate.setFooterView(layoutResId);
    }

    public void setFooterView(View view) {
        mViewDelegate.setFooterView(view);
    }

    public void setFooterFloating(boolean floating) {
        mViewDelegate.setFooterFloating(floating);
    }

    public void setBackgroundRes(@DrawableRes int resId) {
        mViewDelegate.setBackgroundRes(resId);
    }

    public void setBackgroundColor(int color) {
        mViewDelegate.setBackgroundColor(color);
    }

    public void setBackground(Drawable background) {
        mViewDelegate.setBackground(background);
    }

    public void setIdle() {
        mViewDelegate.setState(ViewState.IDLE, null);
    }

    public void setBusy() {
        mViewDelegate.setState(ViewState.BUSY, null);
    }

    public void setBusy(String text) {
        mViewDelegate.setState(ViewState.BUSY, text);
    }

    public void setEmpty() {
        mViewDelegate.setState(ViewState.EMPTY, null);
    }

    public void setEmpty(String text) {
        mViewDelegate.setState(ViewState.EMPTY, text);
    }

    public void setError() {
        mViewDelegate.setState(ViewState.ERROR, null);
    }

    public void setError(String text) {
        mViewDelegate.setState(ViewState.ERROR, text);
    }

    public void setState(ViewState state, String text) {
        mViewDelegate.setState(state, text);
    }

    @Override
    public SupportDelegate getStarterDelegate() {
        return mSupportDelegate;
    }

    public void start(Class<? extends SupportProvider> to) {
        mSupportDelegate.start(new NavIntent(to));
    }

    public void startWithPop(Class<? extends SupportProvider> to, Class<? extends SupportProvider> popTo) {
        NavIntent intent = new NavIntent(to);
        intent.setPopTo(popTo, true);
        mSupportDelegate.start(intent);
    }

    public void startWithPop(Class<? extends SupportProvider> to, Class<? extends SupportProvider> popTo, boolean inclusive) {
        NavIntent intent = new NavIntent(to);
        intent.setPopTo(popTo, inclusive);
        mSupportDelegate.start(intent);
    }

    public void start(NavIntent intent) {
        mSupportDelegate.start(intent);
    }

    public void pop() {
        mSupportDelegate.pop();
    }

    public void popTo(Class<? extends SupportProvider> clazz) {
        mSupportDelegate.popTo(clazz, true);
    }

    public void popTo(Class<? extends SupportProvider> clazz, boolean inclusive) {
        mSupportDelegate.popTo(clazz, inclusive);
    }

    @Override
    public TipDelegate getTipDelegate() {
        return mTipDelegate;
    }

    public void showInfo(String text) {
        mTipDelegate.showInfo(text);
    }

    public void showInfo(String text, int duration) {
        mTipDelegate.showInfo(text, duration);
    }

    public void showWarning(String text) {
        mTipDelegate.showWarning(text);
    }

    public void showWarning(String text, int duration) {
        mTipDelegate.showWarning(text, duration);
    }

    public void showError(String text) {
        mTipDelegate.showError(text);
    }

    public void showError(String text, int duration) {
        mTipDelegate.showError(text, duration);
    }

    public void showSuccess(String text) {
        mTipDelegate.showSuccess(text);
    }

    public void showSuccess(String text, int duration) {
        mTipDelegate.showSuccess(text, duration);
    }

    /**
     * 添加默认标识的Disposable
     * @param disposable Disposable
     */
    public void addDisposable(Disposable disposable) {
        mObservableDelegate.addDisposable(disposable);
    }

    /**
     * 添加默认标识的Disposable
     * @param disposables Disposable数组
     */
    public void addDisposable(Disposable... disposables) {
        mObservableDelegate.addDisposable(disposables);
    }

    /**
     * 添加指定标识的Disposable，null为默认标识
     * @param tag         指定标识
     * @param disposables Disposable数组
     */
    public void addDisposable(Object tag, Disposable... disposables) {
        mObservableDelegate.addDisposable(tag, disposables);
    }

    /**
     * 清除所有的Disposable
     */
    public synchronized void clearDisposables() {
        mObservableDelegate.clearDisposables();
    }

    /**
     * 清除指定标识的Disposable
     * tag为null，清理默认Disposable
     * @param tag 标识
     */
    public synchronized void removeDisposables(Object tag) {
        mObservableDelegate.removeDisposables(tag);
    }

    /**
     * 移除指定标识下的Disposable
     * @param tag         指定标识
     * @param disposables Disposable列表
     */
    public synchronized void removeDisposables(Object tag, Disposable... disposables) {
        mObservableDelegate.removeDisposables(tag, disposables);
    }

    /**
     * 延迟执行任务
     * @param consumer 执行内容
     * @param delay    延迟时间
     */
    public void timer(@NonNull Consumer<Long> consumer, long delay) {
        mObservableDelegate.timer(consumer, delay);
    }

    /**
     * 延迟执行任务
     * @param tag      指定标识
     * @param consumer 执行内容
     * @param delay    延迟时间
     */
    public void timer(Object tag, @NonNull Consumer<Long> consumer, long delay) {
        mObservableDelegate.timer(tag, consumer, delay);
    }

    /**
     * 定时地执行任务（无限次）
     * @param consumer 执行内容
     * @param period   间隔时间
     */
    public void interval(@NonNull Consumer<Long> consumer, long period) {
        mObservableDelegate.interval(consumer, period);
    }

    /**
     * 定时地执行任务
     * @param consumer     执行内容
     * @param initialDelay 初始延迟时间
     * @param period       间隔时间
     */
    public void interval(@NonNull Consumer<Long> consumer, long initialDelay, long period) {
        mObservableDelegate.interval(consumer, initialDelay, period);
    }

    /**
     * 定时地执行任务（无限次）
     * @param tag      指定标识
     * @param consumer 执行内容
     * @param period   间隔时间
     */
    public void interval(Object tag, @NonNull Consumer<Long> consumer, long period) {
        mObservableDelegate.interval(tag, consumer, period);
    }

    /**
     * 定时地执行任务
     * @param tag          指定标识
     * @param consumer     执行内容
     * @param initialDelay 初始延迟时间
     * @param period       间隔时间
     */
    public void interval(Object tag, @NonNull Consumer<Long> consumer, long initialDelay, long period) {
        mObservableDelegate.interval(tag, consumer, initialDelay, period);
    }

    /**
     * 定时地执行任务
     * @param tag          指定标识
     * @param consumer     执行内容
     * @param initialDelay 初始延迟时间
     * @param period       间隔时间
     * @param count        执行次数 <=0为无限次
     */
    public void interval(Object tag, @NonNull Consumer<Long> consumer, long initialDelay, long period, long count) {
        mObservableDelegate.interval(tag, consumer, initialDelay, period, count);
    }
}
