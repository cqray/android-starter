package cn.cqray.android.lifecycle;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

public class ActivityViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity mActivity;

    public ActivityViewModel(AppCompatActivity activity) {
        mActivity = activity;
    }
}
