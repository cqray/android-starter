package cn.cqray.android.strategy;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.Utils;

import cn.cqray.android.R;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 状态栏配置策略
 * @author Cqray
 */
@Getter
@Builder
@Accessors(prefix = "m")
public class ToolbarStrategy {

    /** 标题背景资源 **/
    @Getter(value = AccessLevel.NONE)
    private @Builder.Default @DrawableRes int mBackgroundRes = R.color.colorPrimary;
    /** 标题背景 **/
    private Drawable mBackground;
    /** 标题是否居中 **/
    private @Builder.Default boolean mTitleCenter = false;
    /** 标题是否应用水波纹 **/
    private @Builder.Default boolean mUserRipple = true;
    /** 标题左右间距 **/
    private @Builder.Default float mPadding = getSize(R.dimen.content);
    /** 标题文字颜色 **/
    private @Builder.Default int mTitleTextColor = ColorUtils.getColor(R.color.foreground);
    /** 标题文字大小 **/
    private @Builder.Default float mTitleTextSize = getSize(R.dimen.h2);
    /** 标题文字左右间距 **/
    private @Builder.Default float mTitleSpace = getSize(R.dimen.small);
    /** 标题文字样式 **/
    private Typeface mTitleTypeface;
    /** 标题返回图标资源 **/
    @Getter(value = AccessLevel.NONE)
    private @Builder.Default @DrawableRes int mBackIconRes = R.drawable.def_back_material_light;
    /** 标题返回图标 **/
    private Drawable mBackIcon;
    /** 标题返回左右间距 **/
    private @Builder.Default float mBackSpace = getSize(R.dimen.content);
    /** 标题返回图标颜色 **/
    private @Builder.Default Integer mBackIconTintColor = null;
    /** 标题返回文字 **/
    private String mBackText;
    /** 标题返回文字颜色 **/
    private @Builder.Default int mBackTextColor = ColorUtils.getColor(R.color.foreground);
    /** 标题返回文字大小 **/
    private @Builder.Default float mBackTextSize = getSize(R.dimen.body);
    /** 标题返回文字样式 **/
    private Typeface mBackTypeface;
    /** 标题Action文字颜色 **/
    private @Builder.Default int mActionTextColor = ColorUtils.getColor(R.color.foreground);
    /** 标题Action文字大小 **/
    private @Builder.Default float mActionTextSize = getSize(R.dimen.body);
    /** 标题Action文字样式 **/
    private Typeface mActionTypeface;
    /** 标题分割线颜色 **/
    private @Builder.Default int mDividerColor = ColorUtils.getColor(R.color.divider);
    /** 标题分割线高度 **/
    private @Builder.Default float mDividerHeight = getSize(R.dimen.divider);
    /** 标题分割线左右间隔 **/
    private @Builder.Default float mDividerMargin = 0;
    /** 标题分割线是否显示 **/
    private @Builder.Default boolean mDividerVisible = false;

    public synchronized Drawable getBackground() {
        if (mBackground == null && mBackgroundRes != 0) {
            Context context = Utils.getApp();
            mBackground = ContextCompat.getDrawable(context, mBackgroundRes);
        }
        if (mBackground == null) {
            mBackground = new ColorDrawable(ColorUtils.getColor(R.color.colorPrimary));
        }
        return mBackground;
    }

    public synchronized Drawable getBackIcon() {
        if (mBackIcon == null && mBackIconRes != 0) {
            Context context = Utils.getApp();
            mBackIcon = ContextCompat.getDrawable(context, mBackIconRes);
        }
        if (mBackIcon == null) {
            mBackIcon = ContextCompat.getDrawable(Utils.getApp(), R.drawable.def_back_material_light);
        }
        return mBackIcon;
    }

    private static float getSize(@DimenRes int resId) {
        return Utils.getApp().getResources().getDimension(resId);
    }
}
