package cn.cqray.android.state;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 状态适配器
 * @author Cqray
 */
@Accessors(prefix = "m")
public class StateAdapter implements Serializable {

    /** 是否连接父容器 **/
    private boolean mAttached;
    /** 资源ID **/
    private int mLayoutResId;
    /** 文本内容 **/
    private @Getter String mText;
    /** 根布局 **/
    private @Getter View mContentView;
    /** 刷新控件 **/
    private @Getter StateDelegate mDelegate;
    /** 父容器 **/
    private FrameLayout mParentView;
    /** 事务 **/
    private List<Runnable> mActions = Collections.synchronizedList(new ArrayList<>());

    public StateAdapter(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
    }

    protected void onViewCreated(@NonNull View view) {}

    protected void show(String text) {
        mText = text;
        post(() -> {
            if (mContentView.getParent() == null) {
                mParentView.addView(mContentView);
                mParentView.setVisibility(View.VISIBLE);
                mContentView.bringToFront();
                Log.e("数据", getClass().getName()  + "显示");
            }
        });
    }

    protected void hide() {
        post(() -> {
            mParentView.removeView(mContentView);
            mParentView.setVisibility(View.GONE);
            Log.e("数据", getClass().getName()  + "隐藏显示");
        });
    }

    protected void post(Runnable runnable) {
        if (mAttached) {
            runnable.run();
        } else {
            mActions.add(runnable);
        }
    }

    public void setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
    }

    public void setBackground(final Drawable background) {
        post(() -> ViewCompat.setBackground(mContentView, background));
    }

    void onAttach(StateDelegate delegate, FrameLayout parent) {
        if (mContentView == null) {
            Context context = parent.getContext();
            mAttached = true;
            mDelegate = delegate;
            mParentView = parent;
            mContentView = LayoutInflater.from(context).inflate(mLayoutResId, parent, false);
            for (Runnable action : mActions) {
                action.run();
            }
            mActions.clear();
            onViewCreated(mContentView);
        }
        Log.e("数据", "777-" + (mParentView == null));
    }

    boolean isAttached() {
        return mAttached;
    }
}
