package cn.cqray.android.ui.line;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;
import cn.cqray.android.util.DimenUtils;

/**
 * 文本行
 * @author Cqray
 */
public class TextLineItem extends IconLineItem<TextLineItem> {

    private CharSequence mRightText;
    private int mRightTextColor;
    private int mRightTextColorRes;
    private int mRightTextSize;
    private transient ColorStateList mRightTextColorStateList;

    public TextLineItem(CharSequence text) {
        super(text);
        mRightTextColor = ContextCompat.getColor(getContext(), R.color.tint);
        mRightTextSize = DimenUtils.get(R.dimen.body);
    }

    public TextLineItem rightText(@StringRes int resId) {
        mRightText = getContext().getString(resId);
        return this;
    }

    public TextLineItem rightText(CharSequence text) {
        mRightText = text;
        return this;
    }

    public TextLineItem rightTextColor(int color) {
        mRightTextColor = color;
        mRightTextColorStateList = null;
        return this;
    }

    public TextLineItem rightTextColor(String color) {
        mRightTextColor = Color.parseColor(color);
        mRightTextColorStateList = null;
        return this;
    }

    public TextLineItem rightTextColorRes(@ColorRes int resId) {
        mRightTextColorRes = resId;
        mRightTextColorStateList = null;
        return this;
    }

    public TextLineItem rightTextSize(float size) {
        mRightTextSize = DimenUtils.toPx(size);
        return this;
    }

    public TextLineItem rightTextSizeRes(@DimenRes int resId) {
        mRightTextSize = DimenUtils.get(resId);
        return this;
    }

    public CharSequence getRightText() {
        return mRightText;
    }

    public ColorStateList getRightTextColor() {
        if (mRightTextColorStateList == null) {
            if (mRightTextColorRes == 0) {
                mRightTextColorStateList = ColorStateList.valueOf(mRightTextColor);
            } else {
                mRightTextColorStateList = ContextCompat.getColorStateList(getContext(), mRightTextColorRes);
            }
        }
        return mRightTextColorStateList;
    }

    public int getRightTextSize() {
        return mRightTextSize;
    }

    @Override
    public int getItemType() {
        return LineItem.TEXT;
    }
}
