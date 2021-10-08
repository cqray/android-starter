package cn.cqray.android.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.state.StateRefreshLayout;
import cn.cqray.android.widget.Toolbar;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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

    private final DisposablePool mDisposablePool = new DisposablePool(this);

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

    protected void onCreating(@Nullable Bundle savedInstanceState) {}

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

    public void addDisposable(Disposable disposable) {
        mDisposablePool.add(disposable);
    }

    public void timer(Consumer<Long> consumer) {
        mDisposablePool.timer(consumer, 0);
    }

    public void timer(Consumer<Long> consumer, long delay) {
        mDisposablePool.timer(consumer, delay);
    }

    public Disposable interval(@NonNull Consumer<Long> consumer, long period) {
        return mDisposablePool.interval(consumer, 0, period);
    }

    public Disposable interval(@NonNull Consumer<Long> consumer, long initialDelay, long period) {
        return mDisposablePool.interval(consumer, initialDelay, period);
    }

    /**
     * 有限的循环执行任务
     * @param consumer 执行内容
     * @param start 起始值
     * @param count 循环次数
     * @param initialDelay 初始延迟时间
     * @param period 间隔时间
     */
    public Disposable intervalRange(@NonNull Consumer<Long> consumer, long start, long count, long initialDelay, long period) {
        return mDisposablePool.intervalRange(consumer, start, count, initialDelay, period);
    }
}
