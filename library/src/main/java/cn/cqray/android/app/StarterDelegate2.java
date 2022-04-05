package cn.cqray.android.app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class StarterDelegate2 {

    private FragmentActivity mActivity;
    private Fragment mFragment;

    public StarterDelegate2(FragmentActivity activity) {
        mActivity = activity;
    }

    public StarterDelegate2(Fragment fragment) {
        mFragment = fragment;
    }

    void onCreated() {

    }

    void onDestroyed() {

    }
}
