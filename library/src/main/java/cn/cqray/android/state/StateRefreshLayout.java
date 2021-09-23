package cn.cqray.android.state;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.cqray.android.R;

/**
 * 状态布局控件
 * @author Cqray
 */
public class StateRefreshLayout extends SmartRefreshLayout {

    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private State mCurState = State.IDLE;
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final HashMap<State, StateAdapter> mAdapters = new HashMap<>();

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Set<Map.Entry<State, StateAdapter>> entries = mAdapters.entrySet();
        for (Map.Entry<State, StateAdapter> entry : entries) {
            StateAdapter adapter = entry.getValue();
            if (adapter != null) {
                adapter.onDetachedFromWindow();
            }
        }
    }

    public void setIdle() {
        setState(State.IDLE, null);
    }

    public void setBusy() {
        setState(State.BUSY, null);
    }

    public void setBusy(String text) {
        setState(State.BUSY, text);
    }

    public void setEmpty() {
        setState(State.EMPTY, null);
    }

    public void setEmpty(String text) {
        setState(State.EMPTY, text);
    }

    public void setError() {
        setState(State.ERROR, null);
    }

    public void setError(String text) {
        setState(State.ERROR, text);
    }

    public void setBusyAdapter(StateAdapter adapter) {
        setStateAdapter(State.BUSY, adapter);
    }

    public void setEmptyAdapter(StateAdapter adapter) {
        setStateAdapter(State.EMPTY, adapter);
    }

    public void setErrorAdapter(StateAdapter adapter) {
        setStateAdapter(State.ERROR, adapter);
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
     * 切换状态
     */
    private void setState(State state, String text) {
        saveEnableState();
        mCurState = state;
        // 初始化界面
        initStateLayout();
        if (mCurState != State.BUSY) {
            Set<Map.Entry<State, StateAdapter>> entries = mAdapters.entrySet();
            for (Map.Entry<State, StateAdapter> entry : entries) {
                StateAdapter adapter = entry.getValue();
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

    /**
     * 保存刷新控件状态
     */
    private void saveEnableState() {
        if (mCurState == State.IDLE) {
            mEnableStates[0] = mEnableRefresh;
            mEnableStates[1] = mEnableLoadMore;
            mEnableStates[2] = mEnableOverScrollDrag;
        }
    }

    private void restoreEnableState() {
        if (mCurState == State.IDLE) {
            mEnableRefresh = mEnableStates[0];
            mEnableLoadMore = mEnableStates[1];
            mEnableOverScrollDrag = mEnableStates[2];
        } else {
            mEnableRefresh = mCurState != State.BUSY && mEnableStates[0];
            mManualLoadMore = true;
            mEnableLoadMore = false;
            mEnableOverScrollDrag = false;
        }
    }

    private void setStateAdapter(State state, StateAdapter adapter) {
        StateAdapter sAdapter = mAdapters.get(state);
        if (sAdapter != null) {
            sAdapter.onDetachedFromWindow();
        }
        mAdapters.put(state, adapter);
    }

    @Nullable
    private StateAdapter getAdapter(State state) {
        StateAdapter adapter = mAdapters.get(state);
        if (adapter == null) {
            switch (state) {
                case BUSY:
                    adapter = new BusyAdapter();
                    break;
                case EMPTY:
                    adapter = new EmptyAdapter();
                    break;
                case ERROR:
                    adapter = new ErrorAdapter();
                    break;
                default:
            }
        }
        if (adapter != null) {
            mAdapters.put(state, adapter);
            adapter.onAttach(this, mRootLayout);
        }
        return adapter;
    }

    enum State {
        /** 忙 **/ BUSY,
        /** 空 **/ EMPTY,
        /** 错误 **/ ERROR,
        /** 空闲 **/ IDLE
    }
}
