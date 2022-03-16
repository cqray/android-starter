package cn.cqray.android;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.state.StateAdapter;
import cn.cqray.android.tip.TipAdapter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 框架相关配置策略
 * @author Cqray
 */
@Builder
@Getter
public class StarterStrategy {

    /** Fragment全局默认动画 **/
    private FragmentAnimator fragmentAnimator;
    /** Activity背景资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @DrawableRes int activityBackgroundRes;
    /** Activity背景资源 **/
    private Drawable activityBackground;
    /** Fragment背景资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @DrawableRes int fragmentBackgroundRes;
    /** Fragment背景 **/
    private Drawable fragmentBackground;
    /** 忙布局适配器 **/
    private StateAdapter busyAdapter;
    /** 空布局适配器 **/
    private StateAdapter emptyAdapter;
    /** 错误布局适配器 **/
    private StateAdapter errorAdapter;
    /** 提示适配器 **/
    private TipAdapter tipAdapter;
    /** 标题背景资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @DrawableRes int toolbarBackgroundRes;
    /** 标题背景 **/
    private Drawable toolbarBackground;
    /** 标题是否居中 **/
    private @Builder.Default boolean toolbarTitleCenter = false;
    /** 标题是否应用水波纹 **/
    private @Builder.Default boolean toolbarUserRipple = true;
    /** 标题左右间距 **/
    private @Builder.Default float toolbarPadding = Integer.MIN_VALUE;
    /** 标题文字颜色 **/
    private @Builder.Default int toolbarTitleTextColor = Integer.MIN_VALUE;
    /** 标题文字大小 **/
    private @Builder.Default float toolbarTitleTextSize = Integer.MIN_VALUE;
    /** 标题文字左右间距 **/
    private @Builder.Default float toolbarTitleSpace = Integer.MIN_VALUE;
    /** 标题文字样式 **/
    private Typeface toolbarTitleTypeface;
    /** 标题返回图标资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @DrawableRes int toolbarBackIconRes;
    /** 标题返回图标 **/
    private Drawable toolbarBackIcon;
    /** 标题返回左右间距 **/
    private @Builder.Default float toolbarBackSpace = Integer.MIN_VALUE;
    /** 标题返回图标颜色 **/
    private @Builder.Default int toolbarBackIconTintColor = Integer.MIN_VALUE;
    /** 标题返回文字 **/
    private String toolbarBackText;
    /** 标题返回文字颜色 **/
    private @Builder.Default int toolbarBackTextColor = Integer.MIN_VALUE;
    /** 标题返回文字大小 **/
    private @Builder.Default float toolbarBackTextSize = Integer.MIN_VALUE;
    /** 标题返回文字样式 **/
    private Typeface toolbarBackTypeface;
    /** 标题Action文字颜色 **/
    private @Builder.Default int toolbarActionTextColor = Integer.MIN_VALUE;
    /** 标题Action文字大小 **/
    private @Builder.Default float toolbarActionTextSize = Integer.MIN_VALUE;
    /** 标题Action文字样式 **/
    private Typeface toolbarActionTypeface;
    /** 标题分割线颜色 **/
    private @Builder.Default int toolbarDividerColor = Integer.MIN_VALUE;
    /** 标题分割线高度 **/
    private @Builder.Default float toolbarDividerHeight = Integer.MIN_VALUE;
    /** 标题分割线左右间隔 **/
    private @Builder.Default float toolbarDividerMargin = Integer.MIN_VALUE;
    /** 标题分割线是否显示 **/
    private @Builder.Default boolean toolbarDividerVisible = false;

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

    public synchronized Drawable getToolbarBackground() {
        if (toolbarBackground == null && toolbarBackgroundRes != 0) {
            Context context = Starter.getInstance().getContext();
            toolbarBackground = ContextCompat.getDrawable(context, toolbarBackgroundRes);
        }
        return toolbarBackground;
    }

    public synchronized Drawable getToolbarBackIcon() {
        if (toolbarBackIcon == null && toolbarBackIconRes != 0) {
            Context context = Starter.getInstance().getContext();
            toolbarBackIcon = ContextCompat.getDrawable(context, toolbarBackIconRes);
        }
        return toolbarBackIcon;
    }
}
