package cn.cqray.demo.starter;

import android.os.Bundle;
import android.os.Handler;
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
public class MainFragment extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.activity_main);


//        new StateDelegate().attachProvider(this);
//
//        StateDelegate.get(this).setBusy();


        //mToolbar.setTitleText("车时标题777777");
        //mToolbar.setActionText(0, "确定");
        //mToolbar.setVisibility(View.VISIBLE);
//        setBusy();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setIdle();
//            }
//        }, 1500);
        setBusy("123", "456");
        setIdle();
//        setEmpty("7777", "88888", "99999");
        findViewById(R.id.btn).setOnClickListener(v -> {
            start(MainFragment2.class);
        });
//        setHeaderFloating(false);
    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        setBusy();
//    }


    @Override
    public boolean onBackPressedSupport() {

        Log.e("数据", "MainFragment onBackPressedSupport");
        return super.onBackPressedSupport();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e("数据", "12313213");
    }

}
