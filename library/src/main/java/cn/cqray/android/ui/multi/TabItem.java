package cn.cqray.android.ui.multi;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.StarterProvider;

/**
 * Tab项
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class TabItem {

    private String mName;
    private int mIcon;
    private NavIntent mIntent;

    public TabItem(Class<? extends Fragment> clazz, String name) {
        mIntent = new NavIntent((Class<? extends StarterProvider>) clazz);
        mName = name;
    }

    public TabItem(Class<? extends Fragment> clazz, String name, @DrawableRes int icon) {
        mIntent = new NavIntent((Class<? extends StarterProvider>) clazz);
        mName = name;
        mIcon = icon;
    }

    public TabItem(NavIntent intent, String name) {
        mIntent = intent;
        mName = name;
    }

    public TabItem(NavIntent intent, String name, @DrawableRes int icon) {
        mIntent = intent;
        mName = name;
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public int getIcon() {
        return mIcon;
    }

    public NavIntent getIntent() {
        return mIntent;
    }
}
