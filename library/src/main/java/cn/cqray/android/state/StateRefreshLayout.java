package cn.cqray.android.state;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.R;

/**
 * 状态布局控件
 * @author Cqray
 */
@Deprecated
public class StateRefreshLayout extends SmartRefreshLayout {

    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private ViewState mCurState = ViewState.IDLE;
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final SparseArray<StateAdapter> mAdapters = new SparseArray<>();

    public StateRefreshLayout(Context context) {
        this(context, null);
    }

    public StateRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        @SuppressLint("CustomViewStyleable") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);
        mEnableLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMore, false);
        mEnableOverScrollDrag = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag, true);
        ta.recycle();
        if (getId() == NO_ID) {
            setId(R.id.starter_refresh_layout);
        }

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
        initStateLayout();
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
     * 初始化状态相关界面
     */
    private void initStateLayout() {
        if (mRootLayout != null) {
            return;
        }
        // 初始化界面
        mRootLayout = new FrameLayout(getContext());
        mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRootLayout.setClickable(true);
        mRootLayout.setFocusable(true);
        // 替换布局
        List<View> children = new ArrayList<>();
        FrameLayout refreshContent = new FrameLayout(getContext());
        refreshContent.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof RefreshHeader || view instanceof RefreshFooter) {
                continue;
            }
            children.add(view);
        }
        for (int i = 0; i < children.size(); i++) {
            View view = children.get(i);
            removeView(view);
            refreshContent.addView(view, i);
        }
        refreshContent.addView(mRootLayout);
        setRefreshContent(refreshContent);
    }

    /**
     * 保存刷新控件状态
     */
    private void saveEnableState() {
        if (mCurState == ViewState.IDLE) {
            mEnableStates[0] = mEnableRefresh;
            mEnableStates[1] = mEnableLoadMore;
            mEnableStates[2] = mEnableOverScrollDrag;
        }
    }

    /**
     * 恢复启用状态
     */
    private void restoreEnableState() {
        if (mCurState == ViewState.IDLE) {
            mEnableRefresh = mEnableStates[0];
            mEnableLoadMore = mEnableStates[1];
            mEnableOverScrollDrag = mEnableStates[2];
        } else {
            mEnableRefresh = mCurState != ViewState.BUSY && mEnableStates[0];
            mManualLoadMore = true;
            mEnableLoadMore = false;
            mEnableOverScrollDrag = false;
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
        if (adapter != null && adapter.mContentView == null) {
            mAdapters.put(state.ordinal(), adapter);
            adapter.onAttach(null, mRootLayout);
        }
        return adapter;
    }
}
