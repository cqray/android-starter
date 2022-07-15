package cn.cqray.demo.starter;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;


import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import cn.cqray.android.app.SupportFragment;
import cn.cqray.android.exception.ExceptionActivity;
import cn.cqray.android.launch.Launcher;
import cn.cqray.android.widget.FullTextView;

/**
 * @author Admin
 * @date 2021/9/23 17:31
 */
public class MainFragment extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setNativeContentView(R.layout.activity_main);

        FullTextView tv = findViewById(R.id.tv);

//        new StateDelegate().attachProvider(this);
//
//        StateDelegate.get(this).setBusy();


        //mToolbar.setTitleText("车时标题777777");
        //mToolbar.setActionText(0, "确定");
        //mToolbar.setVisibility(View.VISIBLE);
//        setBusy();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setIdle();
            }
        }, 3500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setBusy();
            }
        }, 1500);
//        setIdle();
//        setEmpty("7777", "88888", "99999");
        findViewById(R.id.btn).setOnClickListener(v -> {
            //startWithPop(MainFragment2.class, MainFragment.class);

            //startActivity(new Intent(requireContext(), ExceptionActivity.class));
            Launcher.with(this).launch(ExceptionActivity.class);
        });


        SmartRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);

        getViewDelegate().getStateDelegate().attachLayout(refreshLayout);


        setEmpty("123", "456");
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
    public void onResume() {
        super.onResume();
        Log.e("数据", "7777" );

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
