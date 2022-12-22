package cn.cqray.android.app2;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import cn.cqray.android.lifecycle.LifecycleViewModelProvider;

/**
 * 持有{@link LifecycleOwner}的ViewModel
 * <p>需要使用{@link LifecycleViewModelProvider}获取</p>
 * @author Cqray
 */
class GetViewModel extends ViewModel {

    @NonNull
    private FragmentActivity activity;

    public GetViewModel(@NonNull FragmentActivity activity) {
        this.activity = activity;
    }

//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        LifecycleViewModelProvider.removeFactory(mLifecycleOwner);
//    }
//
//    @NonNull
//    public LifecycleOwner getLifecycleOwner() {
//        return mLifecycleOwner;
//    }


    @NonNull
    public FragmentActivity getActivity() {
        return activity;
    }
}
