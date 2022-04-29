package cn.cqray.android.state;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class StateDelegate {

    private FrameLayout mNormalLayout;
    private SmartRefreshLayout mRefreshLayout;
    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private ViewState mCurState = ViewState.IDLE;
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final SparseArray<StateAdapter> mAdapters = new SparseArray<>();

    private final Field[] mEnableFields = new Field[4];

    @SneakyThrows
    public StateDelegate() {
        Class<?> cls = SmartRefreshLayout.class;
        mEnableFields[0] = cls.getDeclaredField("mEnableRefresh");
        mEnableFields[1] = cls.getDeclaredField("mEnableLoadMore");
        mEnableFields[2] = cls.getDeclaredField("mEnableOverScrollDrag");
        mEnableFields[3] = cls.getDeclaredField("mManualLoadMore");
    }

    public void attach(SmartRefreshLayout layout) {
        mRefreshLayout = layout;
    }

    public void attach(FrameLayout layout) {
        mNormalLayout = layout;
    }

    public void setIdle() {
        setState(ViewState.IDLE, null);
    }

    public void setBusy() {
        setState(ViewState.BUSY, null);
    }

    public void setBusy(String text) {
        setState(ViewState.BUSY, text);
    }

    public void setEmpty() {
        setState(ViewState.EMPTY, null);
    }

    public void setEmpty(String text) {
        setState(ViewState.EMPTY, text);
    }

    public void setError() {
        setState(ViewState.ERROR, null);
    }

    public void setError(String text) {
        setState(ViewState.ERROR, text);
    }

    public void setState(ViewState state, String text) {
        saveEnableState();
        mCurState = state;
        // 初始化界面
        initRefreshLayout();
        initNormalLayout();
        // 初始化状态
        if (mCurState != ViewState.BUSY) {
            for (int i = 0; i < mAdapters.size(); i++) {
                StateAdapter adapter = mAdapters.valueAt(i);
                if (adapter != null) {
                    adapter.hide();
                }
            }
        }
        // 显示指定状态的界面
        StateAdapter adapter = getAdapter(mCurState);
        if (adapter != null) {
            adapter.show(text);
        }
        restoreEnableState();
    }

    public void setBusyAdapter(StateAdapter adapter) {
        setStateAdapter(ViewState.BUSY, adapter);
    }

    public void setEmptyAdapter(StateAdapter adapter) {
        setStateAdapter(ViewState.EMPTY, adapter);
    }

    public void setErrorAdapter(StateAdapter adapter) {
        setStateAdapter(ViewState.ERROR, adapter);
    }

    /**
     * 初始化刷新界面
     */
    private void initRefreshLayout() {
        if (mRootLayout != null || mRefreshLayout == null) {
            return;
        }
        Context context = mRefreshLayout.getContext();
        // 初始化界面
        mRootLayout = new FrameLayout(context);
        mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRootLayout.setClickable(true);
        mRootLayout.setFocusable(true);
        // 替换布局
        List<View> children = new ArrayList<>();
        FrameLayout refreshContent = new FrameLayout(context);
        refreshContent.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        for (int i = 0; i < mRefreshLayout.getChildCount(); i++) {
            View view = mRefreshLayout.getChildAt(i);
            if (view instanceof RefreshHeader || view instanceof RefreshFooter) {
                continue;
            }
            children.add(view);
        }
        for (int i = 0; i < children.size(); i++) {
            View view = children.get(i);
            mRefreshLayout.removeView(view);
            refreshContent.addView(view, i);
        }
        refreshContent.addView(mRootLayout);
        mRefreshLayout.setRefreshContent(refreshContent);
    }

    /**
     * 初始化常规界面
     */
    private void initNormalLayout() {
        if (mRootLayout != null || mNormalLayout == null) {
            return;
        }
        Context context = mNormalLayout.getContext();
        // 初始化界面
        mRootLayout = new FrameLayout(context);
        mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRootLayout.setClickable(true);
        mRootLayout.setFocusable(true);
        // 常规类型初始化界面
        ViewGroup parent = (ViewGroup) mNormalLayout.getParent();
        parent.removeView(mNormalLayout);
        mRootLayout.addView(mNormalLayout);
        parent.addView(mRootLayout);
    }

    /**
     * 保存刷新控件状态
     */
    @SneakyThrows
    private void saveEnableState() {
        if (mRefreshLayout != null && mCurState == ViewState.IDLE) {
            mEnableStates[0] = mEnableFields[0].getBoolean(mRefreshLayout);
            mEnableStates[1] = mEnableFields[1].getBoolean(mRefreshLayout);
            mEnableStates[2] = mEnableFields[2].getBoolean(mRefreshLayout);
        }
    }

    /**
     * 恢复启用状态
     */
    @SneakyThrows
    private void restoreEnableState() {
        if (mRefreshLayout != null) {
            if (mCurState == ViewState.IDLE) {
                mRefreshLayout.setEnableRefresh(mEnableStates[0]);
                mRefreshLayout.setEnableLoadMore(mEnableStates[1]);
                mRefreshLayout.setEnableOverScrollDrag(mEnableStates[2]);
            } else {
                mRefreshLayout.setEnableRefresh(mCurState != ViewState.BUSY && mEnableStates[0]);
                mRefreshLayout.setEnableLoadMore(false);
                mRefreshLayout.setEnableOverScrollDrag(false);
                mEnableFields[3].set(mRefreshLayout, true);
            }
        }
    }

    /**
     * 设置对应的状态适配器
     * @param state 状态
     * @param adapter 适配器
     */
    private void setStateAdapter(@NonNull ViewState state, StateAdapter adapter) {
        StateAdapter sAdapter = mAdapters.get(state.ordinal());
        if (sAdapter != null) {
            sAdapter.hide();
        }
        mAdapters.put(state.ordinal(), adapter);
    }

    /**
     * 获取对应状态的适配器
     * @param state 指定状态
     */
    @Nullable
    private StateAdapter getAdapter(@NonNull ViewState state) {
        StateAdapter adapter = mAdapters.get(state.ordinal());
        if (adapter == null) {
            if (state == ViewState.BUSY) {
                adapter = new BusyAdapter();
            } else if (state == ViewState.EMPTY) {
                adapter = new EmptyAdapter();
            } else if (state == ViewState.ERROR) {
                adapter = new ErrorAdapter();
            }
        }
        if (adapter != null) {
            mAdapters.put(state.ordinal(), adapter);
            adapter.onAttach(this, mRootLayout);
        }
        return adapter;
    }
}
