package cn.cqray.android.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.state.StateRefreshLayout;
import cn.cqray.android.widget.Toolbar;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 基础Activity
 * @author Cqray
 */
public class SupportActivity extends AppCompatActivity implements StarterProvider {

    /** 设置的布局 **/
    public View mContentView;
    /** 标题 **/
    public Toolbar mToolbar;
    /** 状态刷新控件 **/
    public StateRefreshLayout mRefreshLayout;

    private final ObservableDelegate mObservableDelegate = new ObservableDelegate(this);

    private final ToastDelegate mToastDelegate = new ToastDelegate();
    /** 布局代理 **/
    private final ViewDelegate mViewDelegate = new ViewDelegate(this);
    /** 启动代理 **/
    private final StarterDelegate mStarterDelegate = StarterDelegate.get(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreating(savedInstanceState);
    }

    public void onCreating(@Nullable Bundle savedInstanceState) {}

    @Override
    public void setContentView(View view) {
        mViewDelegate.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResId) {
        mViewDelegate.setContentView(layoutResId);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mViewDelegate.setContentView(view);
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

    @Override
    public StarterDelegate getStarterDelegate() {
        return mStarterDelegate;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return null;
    }

    @Override
    public boolean onBackPressedSupport() {
        return false;
    }

    public void start(Class<? extends StarterProvider> to) {
        mStarterDelegate.start(new NavIntent(to));
    }

    public void startWithPop(Class<? extends StarterProvider> to, Class<? extends StarterProvider> popTo) {
        NavIntent intent = new NavIntent(to);
        intent.setPopTo(popTo, true);
        mStarterDelegate.start(intent);
    }

    public void startWithPop(Class<? extends StarterProvider> to, Class<? extends StarterProvider> popTo, boolean inclusive) {
        NavIntent intent = new NavIntent(to);
        intent.setPopTo(popTo, inclusive);
        mStarterDelegate.start(intent);
    }

    public void start(NavIntent intent) {
        mStarterDelegate.start(intent);
    }

    public boolean pop() {
        return mStarterDelegate.pop();
    }

    public void popOrFinish() {
        if (!pop()) {
            mStarterDelegate.popParent();
        }
    }

    public void popTo(Class<? extends StarterProvider> clazz) {
        mStarterDelegate.popTo(clazz, true);
    }

    public void popTo(Class<? extends StarterProvider> clazz, boolean inclusive) {
        mStarterDelegate.popTo(clazz, inclusive);
    }

    public void setIdle() {
        mViewDelegate.setIdle();
    }

    public void setBusy() {
        mViewDelegate.setBusy(null);
    }

    public void setBusy(String text) {
        mViewDelegate.setBusy(text);
    }

    public void setEmpty() {
        mViewDelegate.setEmpty(null);
    }

    public void setEmpty(String text) {
        mViewDelegate.setEmpty(text);
    }

    public void setError() {
        mViewDelegate.setError(null);
    }

    public void setError(String text) {
        mViewDelegate.setError(text);
    }

    public void showError(String text) {
        mToastDelegate.error(text);
    }

    public void showError(String text, int duration) {
        mToastDelegate.error(text, duration);
    }

    public void showInfo(String text) {
        mToastDelegate.info(text);
    }

    public void showInfo(String text, int duration) {
        mToastDelegate.info(text, duration);
    }

    public void showSuccess(String text) {
        mToastDelegate.success(text);
    }

    public void showSuccess(String text, int duration) {
        mToastDelegate.success(text, duration);
    }

    public void showWarning(String text) {
        mToastDelegate.warning(text);
    }

    public void showWarning(String text, int duration) {
        mToastDelegate.warning(text, duration);
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

    /**
     * 清除所有的Disposable
     */
    public synchronized void clear() {
        mObservableDelegate.clear();
    }

    /**
     * 清除指定标识的Disposable
     * tag为null，清理默认Disposable
     * @param tag 标识
     */
    public synchronized void remove(Object tag) {
        mObservableDelegate.remove(tag);
    }

    /**
     * 移除指定标识下的Disposable
     * @param tag         指定标识
     * @param disposables Disposable列表
     */
    public synchronized void remove(Object tag, Disposable... disposables) {
        mObservableDelegate.remove(tag, disposables);
    }

}
