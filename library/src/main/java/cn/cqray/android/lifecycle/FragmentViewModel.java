package cn.cqray.android.lifecycle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

public class FragmentViewModel extends ViewModel {

    private Fragment mFragment;

    public FragmentViewModel(Fragment fragment) {
        mFragment = fragment;
    }
}
