package cn.cqray.demo.starter;

import android.os.Bundle;

import cn.cqray.android.app.NavActivity;
import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.SupportActivity;

public class MainActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        loadRootFragment(new NavIntent(MainFragment.class));
//        setHeaderView(R.layout.header);
//
//        StateLayout stateLayout = findViewById(R.id.state_layout);
//        stateLayout.setBusy();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                stateLayout.setIdle();
//            }
//        }, 1500);

//        ViewGroup ll = findViewById(R.id.ll);
//
//        ViewGroup content = findViewById(android.R.id.content);
//
//        while (ll != content) {
//
//            Log.e("数据", ll.getClass().getName());
//            ll = (ViewGroup) ll.getParent();
//        }
//        Log.e("数据", content.getClass().getName());
//        Log.e("数据", content.getParent().getClass().getName());

    }
}