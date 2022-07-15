package cn.cqray.android.ui.multi;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import cn.cqray.android.app.SupportProvider;
import cn.cqray.android.app.MultiDelegate;
import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.SupportActivity;

/**
 * 多个Fragment的Activity
 * @author Cqray
 */
public class MultiActivity extends SupportActivity {

    public final MultiDelegate mMultiDelegate = new MultiDelegate(this);

    public void loadMultiFragments(@IdRes int containerId, Fragment... fragments) {
        mMultiDelegate.loadMultiFragments(containerId, fragments);
    }

    public void loadMultiFragments(@IdRes int containerId, NavIntent... intents) {
        mMultiDelegate.loadMultiFragments(containerId, intents);
    }

    public void loadMultiFragemts(@IdRes int containerId, @NonNull Class<? extends SupportProvider>... classes) {
        NavIntent[] intents = new NavIntent[classes.length];
        for (int i = 0; i < classes.length; i++) {
            intents[i] = new NavIntent(classes[i]);
        }
        mMultiDelegate.loadMultiFragments(containerId, intents);
    }

    public void loadMultiFragments(ViewPager2 vp, Fragment... fragments) {
        mMultiDelegate.loadMultiFragments(vp, fragments);
    }

    public void loadMultiFragments(ViewPager2 vp, NavIntent... intents) {
        mMultiDelegate.loadMultiFragments(vp, intents);
    }

    public void loadMultiFragemts(ViewPager2 vp, @NonNull Class<? extends SupportProvider>... classes) {
        NavIntent[] intents = new NavIntent[classes.length];
        for (int i = 0; i < classes.length; i++) {
            intents[i] = new NavIntent((Class<? extends SupportProvider>) classes[i]);
        }
        mMultiDelegate.loadMultiFragments(vp, intents);
    }

    public void showFragment(int position) {
        mMultiDelegate.showFragment(position);
    }

    public void showFragment(Fragment fragment) {
        mMultiDelegate.showFragment(fragment);
    }

    public void addFragment(NavIntent intent) {
        mMultiDelegate.addFragment(intent);
    }

    public void addFragment(Class<? extends SupportProvider> cls) {
        mMultiDelegate.addFragment(cls);
    }

    public void addFragment(Fragment fragment) {
        mMultiDelegate.addFragment(fragment);
    }

    public void removeFragment(int index) {
        mMultiDelegate.removeFragment(index);
    }

    public void removeFragment(Fragment fragment) {
        mMultiDelegate.removeFragment(fragment);
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
