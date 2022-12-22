package cn.cqray.demo.starter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;


import cn.cqray.android.app2.GetViewModel;

public class TestViewModel extends GetViewModel {

    public TestViewModel(@NonNull LifecycleOwner owner) {
        super(owner);

        Log.e("数据", "我哈哈哈");
    }
}
