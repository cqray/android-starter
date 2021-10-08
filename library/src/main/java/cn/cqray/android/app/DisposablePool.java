package cn.cqray.android.app;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 任务池
 * <p>基于RxJava封装<p/>
 * @author Cqray
 */
public class DisposablePool {

    private CompositeDisposable mDisposable;

    public DisposablePool() {}

    public DisposablePool(@NonNull LifecycleOwner owner) {
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                dispose();
            }
        });
    }

    public int count() {
        return getCompositeDisposable().size();
    }

    public boolean isDestroy() {
        if (mDisposable != null) {
            return mDisposable.isDisposed();
        }
        return true;
    }

    public void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    public void addAll(Disposable ...disposables) {
        getCompositeDisposable().addAll(disposables);
    }

    public void clear() {
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    public void dispose() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public void delete(Disposable disposable) {
        getCompositeDisposable().delete(disposable);
    }

    public void remove(Disposable disposable) {
        getCompositeDisposable().remove(disposable);
    }

    /**
     * 延迟执行任务
     * @param consumer 执行内容
     * @param delay 延迟时间
     */
    public void timer(@NonNull Consumer<Long> consumer, long delay) {
        Disposable d = Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        add(d);
    }

    /**
     * 无限循环执行任务
     * @param consumer 执行内容
     * @param period 间隔时间
     */
    public Disposable interval(@NonNull Consumer<Long> consumer, long period) {
        return interval(consumer, period, period);
    }

    /**
     * 无限循环执行任务
     * @param consumer 执行内容
     * @param initialDelay 初始延迟时间
     * @param period 间隔时间
     */
    public Disposable interval(@NonNull Consumer<Long> consumer, long initialDelay, long period) {
        Disposable d = Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        add(d);
        return d;
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
        Disposable d = Observable.intervalRange(start, count, initialDelay, period, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        add(d);
        return d;
    }

    private CompositeDisposable getCompositeDisposable() {
        if (mDisposable == null) {
            synchronized (DisposablePool.class) {
                if (mDisposable == null) {
                    mDisposable = new CompositeDisposable();
                }
            }
        }
        return mDisposable;
    }
}
