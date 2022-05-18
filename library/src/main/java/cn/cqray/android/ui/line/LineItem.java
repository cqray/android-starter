package cn.cqray.android.ui.line;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.cqray.android.R;
import cn.cqray.android.Starter;
import cn.cqray.android.util.ExtUtils;
import cn.cqray.android.util.Sizes;

/**
 * 行项数据基类
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public abstract class LineItem<T extends LineItem<T>> implements MultiItemEntity, Serializable {

    /** 按钮行 **/
    public static final int BUTTON = -200;
    /** 简单图标行 **/
    public static final int ICON = -199;
    /** 文本行 **/
    public static final int TEXT = -198;
    /** 行高 **/
    private int mHeight;
    /** 外部间隔，左上右下 **/
    private int[] mMargin;
    /** 内部间隔，左上右下 **/
    private int[] mPadding;
    /** 分割线高度 **/
    private int mDividerHeight;
    /** 分割线颜色 **/
    private int mDividerColor;
    /** 分割线间隔，左上右下 **/
    private int[] mDividerMargin;
    /** 背景资源 **/
    private int mBackgroundRes;
    /** 标识 **/
    private Object mTag;

    public LineItem() {
        mHeight = Sizes.line();
        mPadding = new int[4];
        mMargin = new int[4];
        // 分割线
        mDividerHeight = Sizes.divider();
        mDividerColor = ContextCompat.getColor(getContext(), R.color.divider);
        mDividerMargin = new int[4];
        // 背景
        mBackgroundRes = R.drawable.bg_line;
        // 赋值
        mPadding[0]
                = mPadding[2]
                = mDividerMargin[0]
                = mDividerMargin[2]
                = Sizes.content();
    }

    public T height(float height) {
        mHeight = ConvertUtils.dp2px(height);
        return (T) this;
    }

    public T margin(float margin) {
        int m = ConvertUtils.dp2px(margin);
        mMargin[0] = m;
        mMargin[1] = m;
        mMargin[2] = m;
        mMargin[3] = m;
        return (T) this;
    }

    public T margin(float l, float r) {
        mMargin[0] = ConvertUtils.dp2px(l);
        mMargin[2] = ConvertUtils.dp2px(r);
        return (T) this;
    }

    public T margin(float l, float t, float r, float b) {
        mMargin[0] = ConvertUtils.dp2px(l);
        mMargin[1] = ConvertUtils.dp2px(t);
        mMargin[2] = ConvertUtils.dp2px(r);
        mMargin[3] = ConvertUtils.dp2px(b);
        return (T) this;
    }

    public T marginTop(float t) {
        mMargin[1] = ConvertUtils.dp2px(t);
        return (T) this;
    }

    public T marginBottom(float b) {
        mMargin[3] = ConvertUtils.dp2px(b);
        return (T) this;
    }

    public T padding(float padding) {
        int p = ConvertUtils.dp2px(padding);
        mPadding[0] = p;
        mPadding[1] = p;
        mPadding[2] = p;
        mPadding[3] = p;
        return (T) this;
    }

    public T padding(float l, float r) {
        mPadding[0] = ConvertUtils.dp2px(l);
        mPadding[2] = ConvertUtils.dp2px(r);
        return (T) this;
    }

    public T padding(float l, float t, float r, float b) {
        mPadding[0] = ConvertUtils.dp2px(l);
        mPadding[1] = ConvertUtils.dp2px(t);
        mPadding[2] = ConvertUtils.dp2px(r);
        mPadding[3] = ConvertUtils.dp2px(b);
        return (T) this;
    }

    public T dividerHeight(float height) {
        mDividerHeight = ConvertUtils.dp2px(height);
        return (T) this;
    }

    public T dividerColor(int color) {
        mDividerColor = color;
        return (T) this;
    }

    public T dividerMargin(float margin) {
        int m = ConvertUtils.dp2px(margin);
        mDividerMargin[0] = m;
        mDividerMargin[1] = m;
        mDividerMargin[2] = m;
        mDividerMargin[3] = m;
        return (T) this;
    }

    public T dividerMargin(float l, float r) {
        mDividerMargin[0] = ConvertUtils.dp2px(l);
        mDividerMargin[2] = ConvertUtils.dp2px(r);
        return (T) this;
    }

    public T dividerMargin(float l, float t, float r, float b) {
        mDividerMargin[0] = ConvertUtils.dp2px(l);
        mDividerMargin[1] = ConvertUtils.dp2px(t);
        mDividerMargin[2] = ConvertUtils.dp2px(r);
        mDividerMargin[3] = ConvertUtils.dp2px(b);
        return (T) this;
    }

    public T backgroundRes(@DrawableRes int resId) {
        mBackgroundRes = resId;
        return (T) this;
    }

    public T tag(Object tag) {
        mTag = tag;
        return (T) this;
    }

    public int getHeight() {
        return mHeight;
    }

    public int[] getMargin() {
        return mMargin;
    }

    public int[] getPadding() {
        return mPadding;
    }

    public int getDividerHeight() {
        return mDividerHeight;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public int[] getDividerMargin() {
        return mDividerMargin;
    }

    public int getBackgroundRes() {
        return mBackgroundRes;
    }

    public Object getTag() {
        return mTag;
    }

    public T copy() {
        try {
            // 写入字节流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            // 读取字节流
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            T o = (T) ois.readObject();
            oos.close();
            baos.close();
            ois.close();
            bais.close();
            return o;
        } catch (IOException | ClassNotFoundException ignored) {
            Gson gson = new Gson();
            return (T) gson.fromJson(gson.toJson(this), getClass());
        }
    }

    protected Context getContext() {
        return Starter.getInstance().getContext();
    }
}
