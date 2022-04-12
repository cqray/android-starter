package cn.cqray.android.lifecycle;

import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

public class SimpleLiveData<T> extends MutableLiveData<T> {

    public SimpleLiveData() {
        super();
    }

    public SimpleLiveData(T value) {
        super(value);
    }

    @Override
    public void setValue(T value) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value);
        } else {
            super.postValue(value);
        }
    }

    @Override
    public void postValue(T value) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value);
        } else {
            super.postValue(value);
        }
    }
}
