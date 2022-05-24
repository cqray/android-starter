package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;


import cn.cqray.android.app.NavActivity;
import cn.cqray.android.ui.multi.MultiItem;
import cn.cqray.android.ui.multi.MultiTabActivity;

public class MainActivity extends MultiTabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadMultiFragments(
                new MultiItem(MainFragment.class, "666"),
                new MultiItem(MainFragment2.class, "777")
        );

        setDragEnable(false);
        ///SupportViewModel viewModel = new ViewModelProvider(this).get(SupportViewModel.class);
        /// Log.e("数据", "" + (viewModel == null));
//        loadMultiFragments(
//                new MultiItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
//                new MultiItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
//        );

//        setNativeContentView(R.layout.test);
//        getSupportDelegate().loadRootFragment(R.id.content, new NavIntent(MainFragment.class));
//        loadRootFragment(MainFragment.class);
//
//
//        Log.e("数据", "7777");
    }

}