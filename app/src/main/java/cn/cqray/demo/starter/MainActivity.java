package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;

import cn.cqray.android.anim.DefaultHorizontalAnimator;
import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.app.NavActivity;

public class MainActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadMultiFragments(
//                new MultiItem(MainFragment.class, "首页"),//, R.drawable.ic_no_data_search),
//                new MultiItem(MainFragment2.class, "我的")//, R.drawable.ic_no_data_search)
//        );

        loadRootFragment(MainFragment.class);

//        new ViewModelProvider(this);
//        new LifecycleViewModelProvider(this);
    }

    @Override
    public boolean onBackPressedSupport() {
        Log.e("数据", "Activity onBackPressedSupport");
        return super.onBackPressedSupport();
    }
}