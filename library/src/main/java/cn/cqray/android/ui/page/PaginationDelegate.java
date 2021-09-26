package cn.cqray.android.ui.page;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import cn.cqray.android.state.StateRefreshLayout;

/**
 * 分页委托
 * @author Cqray
 */
public class PaginationDelegate<T> {

    /** 全局默认起始页码 **/
    private static int sDefaultStartPageNum = 1;
    /** 全局默认分页大小 **/
    private static int sDefaultPageSize = 20;
    /** 起始页码 **/
    private int mStartPageNum = sDefaultStartPageNum;
    /** 分页大小 **/
    private int mPageSize = sDefaultPageSize;
    /** 上次加载的数据对应页码 **/
    private int mLastPageNum = mStartPageNum;
    /** 当前加载的数据对应页码 **/
    private int mCurPageNum = mStartPageNum;
    /** 是否需要满页验证 **/
    private boolean mPaginationFull = true;
    /** 是否可以分页 **/
    private boolean mPaginationEnable = true;
    /** 是否是首次刷新数据 **/
    private boolean mFirstRefresh = true;
    private String mEmptyText;
    private Handler mHandler;
    private StateRefreshLayout mRefreshLayout;
    private RefreshCallback<T> mCallback;
    private BaseQuickAdapter<T, ? extends BaseViewHolder> mAdapter;
    /** 主要是为了不让数据在界面不可见时加载，造成APP卡顿 **/
    private MutableLiveData<List<T>> mData = new MutableLiveData<>();

    public PaginationDelegate(@NonNull LifecycleOwner owner) {
        mHandler = new Handler(Looper.getMainLooper());
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        });
        initDataObserver(owner);
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
                mRefreshLayout.setIdle();
                mFirstRefresh = false;
            }
            // 数据是否为空
            boolean empty = data == null || data.isEmpty();
            if (mCurPageNum == mStartPageNum) {
                if (empty) {
                    // 如果是起始页，数据为空则显示空界面
                    mRefreshLayout.setEmpty(mEmptyText);
                } else {
                    // 显示界面
                    mRefreshLayout.setIdle();
                }
            }
            // 不需要分页
            if (!mPaginationEnable) {
                // 结束刷新数据
                mAdapter.setList(data);
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

    public void setRefreshLayout(StateRefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
        mRefreshLayout.setEnablePureScrollMode(false);
        mRefreshLayout.setEnableRefresh(true);
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
        mPaginationEnable = enable;
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

    public void autoRefresh() {
        check();
        if (mFirstRefresh) {
            mRefreshLayout.setBusy();
            mHandler.postDelayed(this::refreshSilent, 0);
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

    /**
     * 设置全局起始页码
     * @param pageNum 起始页码，默认为1
     */
    public static void setDefaultStartPageNum(int pageNum) {
        sDefaultStartPageNum = pageNum;
    }

    /**
     * 设置全局分页大小
     * @param pageSize 分页大小，默认为20
     */
    public static void setDefaultPageSize(int pageSize) {
        sDefaultPageSize = pageSize;
    }
}
