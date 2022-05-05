package cn.cqray.demo.starter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.cqray.android.app.SupportFragment;
import cn.cqray.android.state.StateDelegate;

/**
 * @author Admin
 * @date 2021/9/23 17:31
 */
public class MainFragment2 extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mToolbar.setActionText(0, "6666");
        mToolbar.setTitle("77777")
                .setDividerVisible(false)
                //.setTitleCenter(true)
                .setActionIcon(1, R.drawable.__android_ic_selected)
                .setActionIconColor(1, Color.RED)
                .setActionListener(1, v -> {
                   showInfo("hello");
                });

        findViewById(R.id.tv).setOnClickListener(v -> {
            Log.e("数据", "点击");
            start(MainFragment.class);
        });

        //StateDelegate.get(this).setEmpty();
        setBusy();

        timer(aLong -> {
            setIdle();
        }, 1500);

//        SmartRefreshLayout frameLayout = findViewById(R.id.content2);
//        StateDelegate delegate = new StateDelegate();
//        delegate.attach(frameLayout);
//        delegate.setBusy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("数据", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("数据", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("数据", "onStop");
    }

    @Override
    public boolean onBackPressedSupport() {

        Log.e("数据", "MainFragment2 onBackPressedSupport");
        return super.onBackPressedSupport();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
