package cn.cqray.android.view;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

/**
 * 布局提供者
 * @author Cqray
 * @date 2022/3/13
 */
public interface ViewProvider {

    ViewDelegate getViewDelegate();

    <T extends View> T findViewById(@IdRes int resId);

    void setContentView(@LayoutRes int layoutResId);

    void setContentView(View view);

    void setNativeContentView(@LayoutRes int layoutResId);

    void setNativeContentView(View view);

    void setHeaderView(@LayoutRes int layoutResId);

    void setHeaderView(View view);

    void setHeaderFloating(boolean floating);

    void setFooterView(@LayoutRes int layoutResId);

    void setFooterView(View view);

    void setFooterFloating(boolean floating);

    void setBackgroundRes(@DrawableRes int resId);

    void setBackgroundColor(int color);

    void setBackground(Drawable background);

    void setIdle();

    void setBusy(String ...texts);

    void setEmpty(String ...texts);

    void setError(String ...texts);
}
