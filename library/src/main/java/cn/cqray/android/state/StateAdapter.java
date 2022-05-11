package cn.cqray.android.state;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.CloneUtils;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 状态适配器
 * @author Cqray
 */
@Accessors(prefix = "m")
public class StateAdapter implements Serializable, Cloneable {

    /** 资源ID **/
    private final int mLayoutResId;
    /** 根布局 **/
    protected  @Getter View mContentView;
    /** 刷新控件 **/
    protected @Getter StateDelegate mDelegate;
    /** 文本内容 **/
    protected MutableLiveData<String> mText = new MutableLiveData<>();
    /** 是否显示 **/
    protected MutableLiveData<Boolean> mShow = new MutableLiveData<>();
    /** 背景 **/
    protected MutableLiveData<Drawable> mBackground = new MutableLiveData<>();
    /** 连接界面 **/
    protected MutableLiveData<FrameLayout> mAttachLayout = new MutableLiveData<>();

    public StateAdapter(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
    }

    protected void onViewCreated(@NonNull View view) {}

    protected void show(String text) {
        mText.setValue(text);
        mShow.setValue(true);
    }

    protected void hide() {
        mShow.setValue(false);
    }

    public void setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
    }

    public void setBackground(final Drawable background) {
        mBackground.setValue(background);
    }

    protected void onTextChanged(String text) {}

    /**
     * 连接界面
     * @param delegate 状态委托
     * @param parent 父容器
     */
    synchronized void onAttach(StateDelegate delegate, FrameLayout parent) {
        if (delegate == null) {
            return;
        }
        LifecycleOwner owner = delegate.getMLifecycleOwner();
        // 监听连接界面变化
        mAttachLayout.observe(owner, layout -> {
            Context context = layout.getContext();
            if (mContentView == null) {
                mDelegate = delegate;
                mContentView = LayoutInflater.from(context).inflate(mLayoutResId, layout, false);
                onViewCreated(mContentView);
            }
        });
        mAttachLayout.setValue(parent);
        // 检查显示或隐藏界面
        mShow.observe(owner, aBoolean -> {
            FrameLayout layout = mAttachLayout.getValue();
            if (layout != null) {
                if (aBoolean) {
                    layout.addView(mContentView);
                    layout.setVisibility(View.VISIBLE);
                    mContentView.bringToFront();
                } else if (mContentView != null) {
                    layout.removeView(mContentView);
                    layout.setVisibility(View.GONE);
                }
            }
        });
        // 监听文本变化
        mText.observe(owner, this::onTextChanged);
        // 监听背景
        mBackground.observe(owner, drawable -> {
            if (mContentView != null) {
                mContentView.setBackground(drawable);
            }
        });
    }

    public void reset() {
        mContentView = null;
        mDelegate = null;
        mAttachLayout = new MutableLiveData<>();
        mBackground = new MutableLiveData<>();
        mShow = new MutableLiveData<>();
        mText = new MutableLiveData<>();
    }

    /**
     * 是否已连接界面
     */
    synchronized boolean isAttached() {
        return mContentView != null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends StateAdapter> T deepClone() {
        T t;
        try {
            t = (T) super.clone();
            t.mContentView = null;
            t.mDelegate = null;
            t.mAttachLayout = new MutableLiveData<>();
            t.mBackground = new MutableLiveData<>();
            t.mShow = new MutableLiveData<>();
            t.mText = new MutableLiveData<>();
        } catch (CloneNotSupportedException ignored) {
            return (T) CloneUtils.deepClone(this, getClass());
        }
        return t;
    }
}
