package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;


import cn.cqray.android.app.NavActivity;

public class MainActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///SupportViewModel viewModel = new ViewModelProvider(this).get(SupportViewModel.class);
        /// Log.e("数据", "" + (viewModel == null));
//        loadMultiFragments(
//                new MultiItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
//                new MultiItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
//        );

//        setNativeContentView(R.layout.test);
//        getSupportDelegate().loadRootFragment(R.id.content, new NavIntent(MainFragment.class));
        loadRootFragment(MainFragment.class);


        Log.e("数据", "7777");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("数据", "8888");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("数据", "11111");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("数据", "9999");
    }

    @Override
    public boolean onBackPressedSupport() {
        Log.e("数据", "Activity onBackPressedSupport");
        return super.onBackPressedSupport();
    }


    @Override
    public void onEnterAnimEnd() {
        Log.e("数据", "动画加载结束");
    }
}