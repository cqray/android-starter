package cn.cqray.android.app;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 观察者代理
 * @author Cqray
 */
public class ObservableDelegate {

    private volatile CompositeDisposable mCompositeDisposable;
    private volatile Map<Object, List<Disposable>> mDisposableMap;

    public ObservableDelegate(@NonNull LifecycleOwner owner) {
        owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                // 释放资源
                if (mCompositeDisposable != null) {
                    mCompositeDisposable.dispose();
                    mCompositeDisposable = null;
                }

                if (mDisposableMap != null) {
                    mDisposableMap.clear();
                    mDisposableMap = null;
                }
            }
        });
    }

    /**
     * 添加默认标识的Disposable
     * @param disposable Disposable
     */
    public void addDisposable(Disposable disposable) {
        addDisposable(null, disposable);
    }

    /**
     * 添加默认标识的Disposable
     * @param disposables Disposable数组
     */
    public void addDisposable(Disposable... disposables) {
        addDisposable(null, disposables);
    }

    /**
     * 添加指定标识的Disposable，null为默认标识
     * @param tag         指定标识
     * @param disposables Disposable数组
     */
    public void addDisposable(Object tag, Disposable... disposables) {
        if (disposables != null && disposables.length > 0) {
            // 获取对应标识下的列表
            List<Disposable> list = getDisposableMap().get(tag);
            if (list == null) {
                list = new ArrayList<>();
            }
            // 遍历添加有效Disposable
            for (Disposable d : disposables) {
                if (d != null) {
                    list.add(d);
                    getCompositeDisposable().add(d);
                }
            }
            // 更新列表数据
            getDisposableMap().put(tag, list);
        }
    }

    /**
     * 延迟执行任务
     * @param consumer 执行内容
     * @param delay    延迟时间
     */
    public void timer(@NonNull Consumer<Long> consumer, long delay) {
        timer(null, consumer, delay);
    }

    /**
     * 延迟执行任务
     * @param tag      指定标识
     * @param consumer 执行内容
     * @param delay    延迟时间
     */
    public void timer(Object tag, @NonNull Consumer<Long> consumer, long delay) {
        interval(tag, consumer, delay, delay, 1);
    }

    /**
     * 定时地执行任务（无限次）
     * @param consumer 执行内容
     * @param period   间隔时间
     */
    public void interval(@NonNull Consumer<Long> consumer, long period) {
        interval(null, consumer, period, period, -1);
    }

    /**
     * 定时地执行任务
     * @param consumer     执行内容
     * @param initialDelay 初始延迟时间
     * @param period       间隔时间
     */
    public void interval(@NonNull Consumer<Long> consumer, long initialDelay, long period) {
        interval(null, consumer, initialDelay, period, -1);
    }

    /**
     * 定时地执行任务（无限次）
     * @param tag      指定标识
     * @param consumer 执行内容
     * @param period   间隔时间
     */
    public void interval(Object tag, @NonNull Consumer<Long> consumer, long period) {
        interval(tag, consumer, period, period, -1);
    }

    /**
     * 定时地执行任务
     * @param tag          指定标识
     * @param consumer     执行内容
     * @param initialDelay 初始延迟时间
     * @param period       间隔时间
     */
    public void interval(Object tag, @NonNull Consumer<Long> consumer, long initialDelay, long period) {
        interval(tag, consumer, initialDelay, period, -1);
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
        final long[] counter = new long[1];
        final Disposable[] disposables = new Disposable[1];
        Disposable d = Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    counter[0]++;
                    consumer.accept(aLong);
                    if (count >= 0 && counter[0] == count) {
                        remove(tag, disposables);
                    }
                });
        disposables[0] = d;
        addDisposable(tag, d);
    }

    /**
     * 清除所有的Disposable
     */
    public synchronized void clear() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        if (mDisposableMap != null) {
            mDisposableMap.clear();
        }
    }

    /**
     * 清除指定标识的Disposable
     * tag为null，清理默认Disposable
     * @param tag 标识
     */
    public synchronized void remove(Object tag) {
        remove(tag, (Disposable[]) null);
    }

    /**
     * 移除指定标识下的Disposable
     * @param tag         指定标识
     * @param disposables Disposable列表
     */
    public synchronized void remove(Object tag, Disposable... disposables) {
        List<Disposable> list = null;
        if (disposables == null || disposables.length == 0) {
            list = getDisposableMap().remove(tag);
        } else {
            List<Disposable> tmp = getDisposableMap().get(tag);
            if (tmp != null && !tmp.isEmpty()) {
                list = new ArrayList<>();
                for (Disposable d : disposables) {
                    if (d != null && tmp.contains(d)) {
                        list.add(d);
                        tmp.remove(d);
                    }
                }
                getDisposableMap().put(tag, tmp);
            }
        }
        // 从CompositeDisposable中移除
        if (list != null && !list.isEmpty()) {
            for (Disposable d : list) {
                getCompositeDisposable().remove(d);
            }
        }
    }

    private Map<Object, List<Disposable>> getDisposableMap() {
        // 初始化Map
        if (mDisposableMap == null) {
            synchronized (ObservableDelegate.class) {
                if (mDisposableMap == null) {
                    mDisposableMap = new HashMap<>(1);
                }
            }
        }
        return mDisposableMap;
    }

    private CompositeDisposable getCompositeDisposable() {
        // 初始化CompositeDisposable
        if (mCompositeDisposable == null) {
            synchronized (ObservableDelegate.class) {
                if (mCompositeDisposable == null) {
                    mCompositeDisposable = new CompositeDisposable();
                }
            }
        }
        return mCompositeDisposable;
    }
}
