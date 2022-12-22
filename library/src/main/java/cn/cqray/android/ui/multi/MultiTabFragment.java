package cn.cqray.android.ui.multi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import cn.cqray.android.R;
import cn.cqray.android.app.MultiDelegate;
import cn.cqray.android.app.GetIntent;
import cn.cqray.android.app.SupportFragment;

/**
 * 底部多Fragment界面
 * @author Cqray
 */
public class MultiTabFragment extends SupportFragment {

    protected ViewPager2 mViewPager;
    protected TabLayout mTabLayout;
    protected final MultiDelegate mMultiDelegate = new MultiDelegate(this);

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setNativeContentView(R.layout.starter_top_tab_layout);
        mViewPager = findViewById(R.id.starter_content_layout);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTabLayout.selectTab(mTabLayout.getTabAt(position), true);
            }
        });
        mTabLayout = findViewById(R.id.starter_tab_layout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mMultiDelegate.showFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void loadMultiFragments(@NonNull MultiItem... items) {
        reset();
        mTabLayout.removeAllTabs();
        GetIntent[] intents = new GetIntent[items.length];
        for (int i = 0; i < items.length; i++) {
            intents[i] = items[i].getIntent();
            MultiItem ti = items[i];
            TabLayout.Tab tab = mTabLayout.newTab();
            if (ti.getIcon() != 0) {
                tab.setIcon(ti.getIcon());
            }
            tab.setText(ti.getName());
            mTabLayout.addTab(tab);
        }
        mMultiDelegate.loadMultiFragments(mViewPager, intents);
    }

    public void addFragment(@NonNull MultiItem item) {
        TabLayout.Tab tab = mTabLayout.newTab();
        if (item.getIcon() != 0) {
            tab.setIcon(item.getIcon());
        }
        tab.setText(item.getName());
        mTabLayout.addTab(tab);
        mMultiDelegate.addFragment(item.getIntent());
    }

    public void removeFragment(int position) {
        mTabLayout.removeTabAt(position);
        mMultiDelegate.removeFragment(position);
    }

    public void setDragEnable(boolean enable) {
        mViewPager.setUserInputEnabled(enable);
    }

    public void showFragment(int index) {
        mMultiDelegate.showFragment(index);
        mTabLayout.selectTab(mTabLayout.getTabAt(index), true);
    }

    public void showFragment(Fragment fragment) {
        mMultiDelegate.showFragment(fragment);
        mTabLayout.selectTab(mTabLayout.getTabAt(getFragments().indexOf(fragment)), true);
    }

    public void reset() {
        mMultiDelegate.reset();
    }

    public int getCurrentIndex() {
        return mMultiDelegate.getCurrentIndex();
    }

    public List<Fragment> getFragments() {
        return mMultiDelegate.getFragments();
    }

    public MultiDelegate getMultiDelegate() {
        return mMultiDelegate;
    }
}
