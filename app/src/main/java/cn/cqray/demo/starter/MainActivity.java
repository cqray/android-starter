package cn.cqray.demo.starter;

import android.os.Bundle;

import cn.cqray.android.ui.multi.BaseMultiActivity;
import cn.cqray.android.ui.multi.BottomMultiActivity;
import cn.cqray.android.ui.multi.TabItem;
import cn.cqray.android.ui.multi.TopTabFragment;

public class MainActivity extends TopTabFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadMultiFragments(
                new TabItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
                new TabItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
        );

        setDragEnable(true);
        showFragment(1);


//        setContentView(R.layout.activity_main);

//        loadRootFragment(MainFragment.class);
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