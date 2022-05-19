package cn.cqray.android;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.Utils;

import cn.cqray.android.anim.DefaultVerticalAnimator;
import cn.cqray.android.anim.FragmentAnimator;
import cn.cqray.android.state.BusyAdapter;
import cn.cqray.android.state.EmptyAdapter;
import cn.cqray.android.state.ErrorAdapter;
import cn.cqray.android.state.StateAdapter;
import cn.cqray.android.tip.TipAdapter;

import cn.cqray.android.util.Sizes;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * 框架相关配置策略
 * @author Cqray
 */
@Builder
@Getter
public class StarterStrategy {

    /** Fragment全局默认动画 **/
    @NonNull
    private @Builder.Default FragmentAnimator fragmentAnimator = new DefaultVerticalAnimator();
    /** Activity背景资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @DrawableRes int activityBackgroundRes;
    /** Activity背景资源 **/
    private Drawable activityBackground;
    /** Fragment背景资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @Builder.Default @DrawableRes int fragmentBackgroundRes = R.color.background;
    /** Fragment背景 **/
    private Drawable fragmentBackground;
    /** 忙布局适配器 **/
    @NonNull
    private @Builder.Default StateAdapter busyAdapter = new BusyAdapter();
    /** 空布局适配器 **/
    @NonNull
    private @Builder.Default StateAdapter emptyAdapter = new EmptyAdapter();
    /** 错误布局适配器 **/
    @NonNull
    private @Builder.Default StateAdapter errorAdapter = new ErrorAdapter();
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
    /** 标题左右间距，单位DP **/
    private @Builder.Default float toolbarPadding = Sizes.contentDp();
    /** 标题文字颜色 **/
    private @Builder.Default int toolbarTitleTextColor = ColorUtils.getColor(R.color.foreground);
    /** 标题文字大小，单位SP **/
    private @Builder.Default float toolbarTitleTextSize = Sizes.h2Sp();
    /** 标题文字左右间距，单位DP **/
    private @Builder.Default float toolbarTitleSpace = Sizes.smallDp();
    /** 标题文字样式 **/
    private Typeface toolbarTitleTypeface;
    /** 标题返回图标资源 **/
    @Getter(value = AccessLevel.PRIVATE)
    private @DrawableRes int toolbarBackIconRes;
    /** 标题返回图标 **/
    private Drawable toolbarBackIcon;
    /** 标题返回左右间距，单位DP **/
    private @Builder.Default float toolbarBackSpace = Sizes.contentDp();
    /** 标题返回图标颜色 **/
    private @Builder.Default Integer toolbarBackIconTintColor = null;
    /** 标题返回文字 **/
    private String toolbarBackText;
    /** 标题返回文字颜色 **/
    private @Builder.Default int toolbarBackTextColor = ColorUtils.getColor(R.color.foreground);
    /** 标题返回文字大小，单位SP **/
    private @Builder.Default float toolbarBackTextSize = Sizes.bodySp();
    /** 标题返回文字样式 **/
    private Typeface toolbarBackTypeface;
    /** 标题Action文字颜色 **/
    private @Builder.Default int toolbarActionTextColor = ColorUtils.getColor(R.color.foreground);
    /** 标题Action文字大小，单位SP **/
    private @Builder.Default float toolbarActionTextSize = Sizes.bodySp();
    /** 标题Action文字样式 **/
    private Typeface toolbarActionTypeface;
    /** 标题分割线颜色 **/
    private @Builder.Default int toolbarDividerColor = ColorUtils.getColor(R.color.divider);
    /** 标题分割线高度，单位DP **/
    private @Builder.Default float toolbarDividerHeight = Sizes.dividerDp();
    /** 标题分割线左右间隔 **/
    private @Builder.Default float toolbarDividerMargin = 0;
    /** 标题分割线是否显示 **/
    private @Builder.Default boolean toolbarDividerVisible = false;
    /** 忙碌状态是否能取消 **/
    private @Builder.Default boolean busyCancelable = true;
    /** 默认分页开始页码 **/
    private @Builder.Default int defaultStartPageNum = 1;
    /** 默认分页大小 **/
    private @Builder.Default int defaultPageSize = 20;

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

        if (toolbarBackground == null) {
            toolbarBackground = new ColorDrawable(ColorUtils.getColor(R.color.colorPrimary));
        }
        return toolbarBackground;
    }

    public synchronized Drawable getToolbarBackIcon() {
        if (toolbarBackIcon == null && toolbarBackIconRes != 0) {
            Context context = Starter.getInstance().getContext();
            toolbarBackIcon = ContextCompat.getDrawable(context, toolbarBackIconRes);
        }
        if (toolbarBackIcon == null) {
            toolbarBackIcon = ContextCompat.getDrawable(Utils.getApp(), R.drawable.def_back_material_light);
        }
        return toolbarBackIcon;
    }

}
