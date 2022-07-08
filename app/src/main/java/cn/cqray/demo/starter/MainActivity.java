package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import cn.cqray.android.app.NavActivity;

public class MainActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        loadMultiFragments(
//                new MultiItem(MainFragment.class, "666"),
//                new MultiItem(MainFragment2.class, "777")
//        );
//
//        setDragEnable(false);
        ///SupportViewModel viewModel = new ViewModelProvider(this).get(SupportViewModel.class);
        /// Log.e("数据", "" + (viewModel == null));
//        loadMultiFragments(
//                new MultiItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
//                new MultiItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
//        );

//        setNativeContentView(R.layout.test);
//        getSupportDelegate().loadRootFragment(R.id.content, new NavIntent(MainFragment.class));
        loadRootFragment(MainFragment.class);
//
//
//        Log.e("数据", "7777");
//        SparseArray<String> array = new SparseArray<>();
//        array.put(10, "10");
//        array.put(1, "3");
//        array.put(2, "4");
//
//        Log.e("数据", array.getClass().getName());
//
//        Traverse.with(array)
//                .valType(String.class)
//                .positive(item -> {
//                    Log.e("数据", "子项:" + item);
//                    return false;
//                });
    }

}