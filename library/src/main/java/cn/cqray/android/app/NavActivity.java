package cn.cqray.android.app;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.cqray.android.R;

/**
 * 导航入口界面
 * @author Cqray
 */
public class NavActivity extends SupportActivity {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setNativeContentView(R.layout.starter_navigation_layout);
    }

    public void loadRootFragment(NavIntent intent) {
        getStarterDelegate().loadRootFragment(R.id.starter_navigation_layout, intent);
    }
}
