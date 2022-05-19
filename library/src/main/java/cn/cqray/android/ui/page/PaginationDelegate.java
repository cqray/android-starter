package cn.cqray.android.ui.page;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.state.StateDelegate;

/**
 * 分页委托
 * @author Cqray
 */
public class PaginationDelegate<T> {
    /** 起始页码 **/
    private int mStartPageNum;
    /** 分页大小 **/
    private int mPageSize;
    /** 上次加载的数据对应页码 **/
    private int mLastPageNum;
    /** 当前加载的数据对应页码 **/
    private int mCurPageNum;
    /** 是否需要满页验证 **/
    private boolean mPaginationFull = true;
    /** 是否是首次刷新数据 **/
    private boolean mFirstRefresh = true;
    private String mEmptyText;
    private SmartRefreshLayout mRefreshLayout;
    private RefreshCallback<T> mCallback;
    /** 列表适配器 **/
    private BaseQuickAdapter<T, ? extends BaseViewHolder> mAdapter;
    /** 主要是为了不让数据在界面不可见时加载，造成APP卡顿 **/
    private final MutableLiveData<List<T>> mData = new MutableLiveData<>();
    /** 是否可以分页 **/
    private final MutableLiveData<Boolean> mPaginationEnable = new MutableLiveData<>();
    /** 状态管理委托 **/
    private final StateDelegate mStateDelegate;

    public PaginationDelegate(@NonNull LifecycleOwner owner) {
        // 初始化相关配置参数
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        mStartPageNum = strategy.getDefaultStartPageNum();
        mPageSize = strategy.getDefaultPageSize();
        mLastPageNum = mStartPageNum;
        mCurPageNum = mStartPageNum;

        initDataObserver(owner);
        mStateDelegate = StateDelegate.get(owner);

        mPaginationEnable.observe(owner, aBoolean -> {
            if (mRefreshLayout != null) {
                mRefreshLayout.setEnableLoadMore(aBoolean);
            }
        });
    }

    private void initDataObserver(LifecycleOwner owner) {
        mData.observe(owner, data -> {
            // 结束控件刷新
            if (mRefreshLayout.isRefreshing()) {
                mRefreshLayout.finishRefresh();
            }
            // 结束控件加载更多
            if (mRefreshLayout.isLoading()) {
                mRefreshLayout.finishLoadMore();
            }
            if (mFirstRefresh) {
                mStateDelegate.setIdle();
                mFirstRefresh = false;
            }
            // 数据是否为空
            boolean empty = data == null || data.isEmpty();
            if (mCurPageNum == mStartPageNum) {
                if (empty) {
                    // 如果是起始页，数据为空则显示空界面
                    mStateDelegate.setEmpty(mEmptyText);
                } else {
                    // 显示界面
                    mStateDelegate.setIdle();
                }
            }
            // 不需要分页
            if (!isPaginationEnable()) {
                // 结束刷新数据
                mAdapter.setList(data);
                // 重复设置上拉刷新状态，避免中途有修改
                mRefreshLayout.setEnableLoadMore(false);
                return;
            }
            // 上次数据页面和本次不能衔接到一起，则做无效处理
            if (mCurPageNum - mLastPageNum > 1) {
                return;
            }
            // 是否有更多数据
            boolean noMoreData = empty || (mPaginationFull && data.size() < mPageSize);
            mRefreshLayout.setNoMoreData(noMoreData);
            // 如果是第一页
            if (mCurPageNum == mStartPageNum) {
                // 刷新数据
                mAdapter.setList(data);
            } else {
                // 加载数据
                if (!empty) {
                    mAdapter.addData(data);
                }
            }
            // 记录页码
            mLastPageNum = mCurPageNum;
        });
    }

    public void reset() {
        mCurPageNum = mStartPageNum;
        mFirstRefresh = true;
    }

    private void check() {
        if (mRefreshLayout == null) {
            throw new IllegalStateException("You should call setRefreshLayout before.");
        }
        if (mAdapter == null) {
            throw new IllegalStateException("You should call setAdapter before.");
        }
    }

    public void setRefreshCallback(RefreshCallback<T> callback) {
        mCallback = callback;
    }

    public void setRefreshLayout(SmartRefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
        mRefreshLayout.setEnablePureScrollMode(false);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setEnableOverScrollDrag(true);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mCurPageNum ++;
                if (mCallback != null) {
                    mCallback.onRefresh(PaginationDelegate.this, mCurPageNum, mPageSize);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mCurPageNum = mStartPageNum;
                if (mCallback != null) {
                    mCallback.onRefresh(PaginationDelegate.this, mCurPageNum, mPageSize);
                }
            }
        });
        mStateDelegate.attachLayout(refreshLayout);
    }

    public void setAdapter(BaseQuickAdapter<T, ? extends BaseViewHolder> adapter) {
        mAdapter = adapter;
    }

    /**
     * 设置起始页码
     * @param pageNum 起始页码，默认为1
     */
    public void setStartPageNum(int pageNum) {
        mStartPageNum = pageNum;
    }

    /**
     * 设置分页大小
     * @param pageSize 分页大小，默认为20
     */
    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    /**
     * 设置是否开启分页功能。
     * 设置true，表示可以下拉加载更多。
     * 设置false，表示不可以下拉加载更多。
     * @param enable 是否开启分页功能， 默认为true
     */
    public void setPaginationEnable(boolean enable) {
        mPaginationEnable.setValue(enable);
    }

    /**
     * 设置分页时是否充满每页。
     * 设置true，表示一页未满指定数量，则没有更多数据。
     * 设置false，表示只有遇到数据为空，才会设置为没有更多数据。
     * @param full 是否充满每页，默认为true
     */
    public void setPaginationFull(boolean full) {
        mPaginationFull = full;
    }

    public void setDefaultEmptyText(String text) {
        mEmptyText = text;
    }

    public void finish(List<T> data) {
        check();
        mData.setValue(data);
    }

    public void finishWithException(Throwable throwable) {
        check();
        mData.setValue(null);
        mStateDelegate.setError(throwable == null ? null : throwable.getMessage());
    }

    public void autoRefresh() {
        check();
        if (mFirstRefresh) {
            mStateDelegate.setBusy();
            mRefreshLayout.post(this::refreshSilent);
        } else {
            mRefreshLayout.autoRefresh();
        }
    }

    public void refreshSilent() {
        if (mCallback != null) {
            mCurPageNum = 1;
            mCallback.onRefresh(this, mCurPageNum, mPageSize);
        }
    }

    public boolean isPaginationEnable() {
        return mPaginationEnable.getValue() == null ? true : mPaginationEnable.getValue();
    }
}
