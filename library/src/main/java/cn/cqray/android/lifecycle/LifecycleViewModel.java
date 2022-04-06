package cn.cqray.android.lifecycle;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

/**
 * 持有{@link LifecycleOwner}的ViewModel
 * <p>需要使用{@link LifecycleViewModelProvider}获取</p>
 * @author Cqray
 */
public class LifecycleViewModel extends ViewModel {

    @NonNull
    private LifecycleOwner mLifecycleOwner;

    public LifecycleViewModel(@NonNull LifecycleOwner owner) {
        mLifecycleOwner = owner;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LifecycleViewModelProvider.removeFactory(mLifecycleOwner);
    }

    @NonNull
    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }
}
