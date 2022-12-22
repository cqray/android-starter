package cn.cqray.android.app2;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import cn.cqray.android.lifecycle.LifecycleViewModelProvider;

/**
 * 持有{@link LifecycleOwner}的ViewModel
 * <p>需要使用{@link LifecycleViewModelProvider}获取</p>
 * @author Cqray
 */
public class GetViewModel extends ViewModel implements DefaultLifecycleObserver {

    @NonNull
    private final LifecycleOwner mLifecycleOwner;

    public GetViewModel(@NonNull LifecycleOwner owner) {
        mLifecycleOwner = owner;
    }

    @NonNull
    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }
}
