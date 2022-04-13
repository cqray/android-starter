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
//        getStarterDelegate().loadRootFragment(R.id.content, new NavIntent(MainFragment.class));
        loadRootFragment(MainFragment2.class);
//        findViewById(R.id.tv).setOnClickListener(v -> {
//            showInfo("6666");
//        });

//        new ViewModelProvider(this);
//        new LifecycleViewModelProvider(this);

//        ToolbarStrategy strategy = ToolbarStrategy.builder()
//                .titleCenter(true)
//                .background()
    }

    @Override
    public boolean onBackPressedSupport() {
        Log.e("数据", "Activity onBackPressedSupport");
        return super.onBackPressedSupport();
    }

    //    @Override
//    public void onBackPressed() {
//        Log.e("数据", "Activity onBackPressed");
//        super.onBackPressed();
//    }
}