package cn.cqray.android;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.app.ToastDelegate;
import cn.cqray.android.state.StateAdapter;
import lombok.Builder;
import lombok.Getter;

/**
 * 框架相关配置策略
 * @author Cqray
 */
@Builder
public class StarterStrategy {

    /** Fragment全局默认动画 **/
    private @Getter FragmentAnimator fragmentAnimator;
    /** Activity背景资源 **/
    private @DrawableRes int activityBackgroundRes;
    /** Activity背景资源 **/
    private Drawable activityBackground;
    /** Fragment背景资源 **/
    private @DrawableRes int fragmentBackgroundRes;
    /** Fragment背景 **/
    private Drawable fragmentBackground;
    /** 忙布局适配器 **/
    private @Getter StateAdapter busyAdapter;
    /** 空布局适配器 **/
    private @Getter StateAdapter emptyAdapter;
    /** 错误布局适配器 **/
    private @Getter StateAdapter errorAdapter;
    @Getter
    private ToastDelegate.Adapter toastAdapter;

    private Drawable toolbarBackground;
    private int toolbarTitleTextColor;
    private boolean toolbarTitleCenter;
    private boolean toolbarUserRipple;
    private float toolbarPadding;


    public synchronized Drawable getActivityBackground() {
        if (activityBackground == null && activityBackgroundRes != 0) {
            Context context = Starter.getInstance().getContext();
            activityBackground = ContextCompat.getDrawable(context, activityBackgroundRes);
        }
        return activityBackground;
    }

    public synchronized Drawable getFragmentBackground() {
        if (fragmentBackground == null && fragmentBackgroundRes != 0) {
            Context context = Starter.getInstance().getContext();
            fragmentBackground = ContextCompat.getDrawable(context, fragmentBackgroundRes);
        }
        return fragmentBackground;
    }
}
