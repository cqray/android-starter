package cn.cqray.android.state;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.CloneUtils;

import java.io.Serializable;

import cn.cqray.android.util.ContextUtils;
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

    public void setBackground(final Drawable background) {
        mBackground.setValue(background);
    }

    public void setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
    }

    public void setBackgroundResource(@DrawableRes int resId) {
        setBackground(ContextCompat.getDrawable(ContextUtils.get(), resId));
    }

    protected void onTextChanged(String text) {}

    /**
     * 关联界面
     * @param delegate 状态委托
     * @param parent 父容器
     */
    synchronized void onAttach(StateDelegate delegate, FrameLayout parent) {
        if (delegate == null) {
            return;
        }
        LifecycleOwner owner = delegate.getLifecycleOwner();
        // 监听连接界面变化
        mAttachLayout.observe(owner, layout -> {
            Context context = layout.getContext();
            // 初始化界面
            if (mContentView == null) {
                mDelegate = delegate;
                mContentView = LayoutInflater.from(context).inflate(mLayoutResId, layout, false);
                onViewCreated(mContentView);
            }
        });
        // 关联界面
        mAttachLayout.setValue(parent);
        // 检查显示或隐藏界面
        mShow.observe(owner, aBoolean -> {
            FrameLayout layout = mAttachLayout.getValue();
            if (layout != null) {
                // 关联了界面才进行显示或隐藏操作
                if (aBoolean) {
                    // 显示并隐藏相应的界面
                    if (mContentView.getParent() == null) {
                        layout.addView(mContentView);
                        layout.setVisibility(View.VISIBLE);
                        mContentView.bringToFront();
                    }
                } else if (mContentView != null) {
                    // 隐藏并移除相应的界面
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

    /**
     * 重置状态适配器
     */
    public void reset() {
        mContentView = null;
        mDelegate = null;
        mAttachLayout = new MutableLiveData<>();
        mBackground = new MutableLiveData<>();
        mShow = new MutableLiveData<>();
        mText = new MutableLiveData<>();
    }

    /**
     * 深度拷贝状态适配器
     * @param <T> 泛型
     * @return 实例
     */
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
