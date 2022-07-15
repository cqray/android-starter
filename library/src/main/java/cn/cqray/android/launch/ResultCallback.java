package cn.cqray.android.launch;

import android.content.Intent;

import androidx.annotation.NonNull;

public interface ResultCallback {

    void onSucceed(@NonNull Intent intent);

    default void onFail() {}
}
