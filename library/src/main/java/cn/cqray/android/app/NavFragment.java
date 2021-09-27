package cn.cqray.android.app;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.cqray.android.R;

/**
 * 导航入口界面
 * @author Cqray
 */
public class NavFragment extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setNativeContentView(R.layout.starter_navigation_layout);
    }

    public void loadRootFragment(Class<? extends SupportFragment> fragmentClass) {
        NavIntent intent = new NavIntent(fragmentClass);
        getStarterDelegate().loadRootFragment(R.id.starter_content_layout, intent);
    }

    public void loadRootFragment(NavIntent intent) {
        getStarterDelegate().loadRootFragment(R.id.starter_content_layout, intent);
    }
}
