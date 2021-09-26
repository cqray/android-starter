package cn.cqray.android.ui.multi;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import cn.cqray.android.R;
import cn.cqray.android.app.MultiDelegate;
import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.SupportActivity;

/**
 * 底部多Fragment界面
 * @author Cqray
 */
public class BottomMultiActivity extends SupportActivity {

    protected ViewPager2 mViewPager;
    protected BottomNavigationView mNavigationView;
    protected final MultiDelegate mMultiDelegate = new MultiDelegate(this);

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setNativeContentView(R.layout.starter_bottom_multi_layout);
        mViewPager = findViewById(R.id.starter_content_layout);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mNavigationView.setSelectedItemId(position);
            }
        });
        mNavigationView = findViewById(R.id.starter_navigation_view);
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
            mMultiDelegate.showFragment(item.getOrder());
            item.setChecked(true);
            return false;
        });
    }

    public void loadMultiFragments(@NonNull TabItem... items) {
        reset();
        Menu menu = mNavigationView.getMenu();
        menu.clear();
        NavIntent[] intents = new NavIntent[items.length];
        for (int i = 0; i < items.length; i++) {
            intents[i] = items[i].getIntent();
            TabItem ti = items[i];
            menu.add(0, i, i, ti.getName()).setIcon(ti.getIcon());
        }
        mMultiDelegate.loadMultiFragments(mViewPager, intents);
    }

    public void setDragEnable(boolean enable) {
        mViewPager.setUserInputEnabled(enable);
    }

    public void showFragment(int index) {
        mMultiDelegate.showFragment(index);
        mNavigationView.setSelectedItemId(index);
    }

    public void showFragment(Fragment fragment) {
        mMultiDelegate.showFragment(fragment);
        mNavigationView.setSelectedItemId(getFragments().indexOf(fragment));
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
