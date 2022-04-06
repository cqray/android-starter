package cn.cqray.android.app;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import cn.cqray.android.lifecycle.LifecycleViewModel;

public class LifecycleViewModel2 extends LifecycleViewModel {

    public LifecycleViewModel2(LifecycleOwner owner) {
        super(owner);
        Log.e("数据", "666666666");
    }
}
