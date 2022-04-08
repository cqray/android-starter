package cn.cqray.android.ui.multi;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import cn.cqray.android.app.SupportProvider;
import cn.cqray.android.app.NavIntent;

/**
 * Tab项
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class MultiItem {

    private String mName;
    private int mIcon;
    private NavIntent mIntent;

    public MultiItem(Class<? extends Fragment> clazz, String name) {
        mIntent = new NavIntent((Class<? extends SupportProvider>) clazz);
        mName = name;
    }

    public MultiItem(Class<? extends Fragment> clazz, String name, @DrawableRes int icon) {
        mIntent = new NavIntent((Class<? extends SupportProvider>) clazz);
        mName = name;
        mIcon = icon;
    }

    public MultiItem(NavIntent intent, String name) {
        mIntent = intent;
        mName = name;
    }

    public MultiItem(NavIntent intent, String name, @DrawableRes int icon) {
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
