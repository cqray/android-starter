package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import cn.cqray.android.app.NavActivity;
import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.StarterViewModel;
import cn.cqray.android.app.SupportActivity;
import cn.cqray.android.ui.multi.MultiItem;
import cn.cqray.android.ui.multi.MultiTabActivity;
import cn.cqray.android.ui.multi.MultiTabFragment;

public class MainActivity extends MultiTabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///StarterViewModel viewModel = new ViewModelProvider(this).get(StarterViewModel.class);
        /// Log.e("数据", "" + (viewModel == null));
        loadMultiFragments(
                new MultiItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
                new MultiItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
        );

//        setNativeContentView(R.layout.test);
//        getStarterDelegate().loadRootFragment(R.id.content, new NavIntent(MainFragment.class));
        //loadRootFragment(MainFragment.class);
//        findViewById(R.id.tv).setOnClickListener(v -> {
//            showInfo("6666");
//        });

//        new ViewModelProvider(this);
//        new LifecycleViewModelProvider(this);
    }

    @Override
    public boolean onBackPressedSupport() {
        Log.e("数据", "Activity onBackPressedSupport");
        return super.onBackPressedSupport();
    }
}