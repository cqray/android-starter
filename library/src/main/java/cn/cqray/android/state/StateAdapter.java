package cn.cqray.android.state;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.text.TextUtils;
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
@SuppressWarnings("unchecked")
@Accessors(prefix = "m")
public abstract class StateAdapter<T extends StateAdapter<T>> implements Serializable, Cloneable {

    /** 资源ID **/
    private final int mLayoutResId;
    /** 继承检查，用于检查继承后是否重写reset及deepClone方法 **/
    private boolean mExtendsCheck;
    /** 默认文本内容 **/
    protected String mDefaultText;
    /** 根布局 **/
    protected @Getter View mContentView;
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

    protected void makeSureOverridden() {
        mExtendsCheck = true;
    }

    protected final void checkExtends() {
        if (!mExtendsCheck) {
            String superClassName = getClass().getSuperclass() == null
                    ? StateAdapter.class.getSimpleName()
                    : getClass().getSuperclass().getSimpleName();
            throw new RuntimeException(String.format("\nWhen %s extends the %s class,\n", getClass().getSimpleName(), superClassName) +
                    "reset() and deepClone() should be overridden to ensure that \n" +
                    "the newly defined additional fields work properly after the adapter is reset or cloned, \n" +
                    "and then makeSureOverridden() should be called in the constructor.");
        }
    }

    protected void show(String text) {
        checkExtends();
        mText.setValue(text);
        mShow.setValue(true);
    }

    protected void hide() {
        checkExtends();
        mShow.setValue(false);
    }

    public T setBackground(final Drawable background) {
        mBackground.setValue(background);
        return (T) this;
    }

    public T setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
        return (T) this;
    }

    public T setBackgroundResource(@DrawableRes int resId) {
        setBackground(ContextCompat.getDrawable(ContextUtils.get(), resId));
        return (T) this;
    }

    public T setDefaultText(String text) {
        mDefaultText = text;
        return (T) this;
    }

    /**
     * 控件创建完毕
     * @param view 根控件
     */
    protected void onViewCreated(@NonNull View view) {}

    /**
     * 文本内容发生了变化
     * @param text 文本内容
     */
    protected abstract void onTextChanged(String text);

    /**
     * 背景发生了变化
     * @param background 背景
     */
    protected abstract void onBackgroundChanged(Drawable background);

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
        mText.observe(owner, s -> {
            String text = TextUtils.isEmpty(s) ? mDefaultText : s;
            onTextChanged(text);
        });
        // 监听背景
        mBackground.observe(owner, this::onBackgroundChanged);
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
     * @param <S> 泛型
     * @return 实例
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <S extends StateAdapter<S>> S deepClone() {
        S s;
        try {
            s = (S) super.clone();
            s.mContentView = null;
            s.mDelegate = null;
            s.mAttachLayout = new MutableLiveData<>();
            s.mBackground = new MutableLiveData<>();
            s.mShow = new MutableLiveData<>();
            s.mText = new MutableLiveData<>();
        } catch (CloneNotSupportedException ignored) {
            return (S) CloneUtils.deepClone(this, getClass());
        }
        return s;
    }

}
