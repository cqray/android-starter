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

    public void loadRootFragment(Class<? extends SupportProvider> fragmentClass) {
        GetIntent intent = new GetIntent(fragmentClass);
        getSupportDelegate().loadRootFragment(R.id.starter_content_layout, intent);
    }

    public void loadRootFragment(GetIntent intent) {
        getSupportDelegate().loadRootFragment(R.id.starter_content_layout, intent);
    }
}
