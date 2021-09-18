package cn.cqray.android.state;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.io.Serializable;
import java.util.Vector;

/**
 * 状态适配器
 * @author Cqray
 */
public class StateAdapter implements Serializable {

    /** 是否连接父容器 **/
    private boolean mAttached;
    /** 资源ID **/
    private int mLayoutResId;
    /** 文本内容 **/
    private String mText;
    /** 根布局 **/
    private View mRootView;
    /** 父容器 **/
    private FrameLayout mParentView;
    /** 刷新控件 **/
    private StateRefreshLayout mRefreshLayout;
    /** 事务 **/
    private Vector<Runnable> mActions = new Vector<>();

    public StateAdapter(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
    }

    protected void onAttachedToWindow() {}

    protected void onDetachedFromWindow() {
        if (mParentView != null && mRootView != null) {
            mParentView.removeView(mRootView);
            mRootView = null;
        }
    }

    protected void onViewCreated(@NonNull View view) {}

    protected void show(String text) {
        mText = text;
        post(() -> {
            if (mRootView.getParent() == null) {
                mParentView.addView(mRootView);
                mRootView.bringToFront();
            }
        });
    }

    protected void hide() {
        post(() -> {
            mParentView.removeView(mRootView);
            mParentView.setVisibility(View.GONE);
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
        post(() -> ViewCompat.setBackground(mRootView, background));
    }

    public String getText() {
        return mText;
    }

    public StateRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public View getView() {
        return mRootView;
    }

    void onAttach(StateRefreshLayout refreshLayout, FrameLayout parent) {
        if (mRootView == null) {
            Context context = parent.getContext();
            mAttached = true;
            mParentView = parent;
            mRefreshLayout = refreshLayout;
            mRootView = LayoutInflater.from(context).inflate(mLayoutResId, parent, false);
            for (Runnable action : mActions) {
                action.run();
            }
            mActions.clear();
            onAttachedToWindow();
            onViewCreated(mRootView);
        }
    }
}
