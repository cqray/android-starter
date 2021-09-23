package cn.cqray.demo.starter;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.cqray.android.app.SupportFragment;

/**
 * @author Admin
 * @date 2021/9/23 17:31
 */
public class MainFragment2 extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
