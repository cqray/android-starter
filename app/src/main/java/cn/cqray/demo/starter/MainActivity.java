package cn.cqray.demo.starter;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import cn.cqray.android.app.NavActivity;
import cn.cqray.android.tip.TipDelegate;
import cn.cqray.android.ui.multi.MultiItem;
import cn.cqray.android.ui.multi.MultiTabActivity;

public class MainActivity extends MultiTabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMultiFragments(
                new MultiItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
                new MultiItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
        );

//        loadRootFragment(MainFragment.class);
    }

    @Override
    public boolean onBackPressedSupport() {
        Log.e("数据", "Activity onBackPressedSupport");
        return super.onBackPressedSupport();
    }
}