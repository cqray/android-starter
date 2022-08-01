package cn.cqray.android.ui.page;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import cn.cqray.android.R;
import cn.cqray.android.app.SupportFragment;
import cn.cqray.android.object.ResponseData;
import lombok.Getter;

/**
 * 分页Activity
 * @author Cqray
 */
public abstract class PaginationFragment<T> extends SupportFragment {

//    /** 动画结束后再刷新 **/
//    protected boolean mRefreshAfterEnterAnim;
    /** 列表控件 **/
    protected RecyclerView mRecyclerView;
    /** 数据适配器 **/
    protected @Getter BaseQuickAdapter<T, ? extends BaseViewHolder> mAdapter;
    /** 分页管理委托 **/
    public final PaginationDelegate<T> mPaginationDelegate = new PaginationDelegate<>(this);

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.starter_pagination_layout);
        mRecyclerView = findViewById(R.id.__android_recycler);
        // 初始化适配器
        mAdapter = onCreateAdapter();
        mAdapter.setRecyclerView(mRecyclerView);
        // 初始化列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mAdapter);
        // 初始化分页委托
        mPaginationDelegate.setRefreshLayout(mRefreshLayout);
        mPaginationDelegate.setAdapter(mAdapter);
        mPaginationDelegate.setRefreshCallback((delegate, pageNum, pageSize) -> PaginationFragment.this.onRefresh(pageNum, pageSize));
    }

    @Override
    public void onEnterAnimEnd() {
        autoRefresh();
    }

    /**
     * 创建Adapter
     * @return Adapter
     */
    protected abstract BaseQuickAdapter<T, ? extends BaseViewHolder> onCreateAdapter();

    /**
     * 刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected abstract void onRefresh(int pageNum, int pageSize);

    public void setEnableDrag(boolean enable) {
        mRefreshLayout.setEnableLoadMore(enable);
        mRefreshLayout.setEnableRefresh(enable);
        mRefreshLayout.setEnableOverScrollDrag(enable);
    }

    public void setDefaultEmptyText(String text) {
        mPaginationDelegate.setDefaultEmptyText(text);
    }

    public void setPureScrollMode() {
        mRefreshLayout.setEnablePureScrollMode(true);
    }

    /**
     * 自动刷新数据
     */
    public void autoRefresh() {
        mPaginationDelegate.autoRefresh();
    }

    /**
     * 静默刷新数据，没有动画
     */
    public void refreshSilent() {
        mPaginationDelegate.refreshSilent();
    }

    /**
     * 数据请求结束
     * @param data 数据
     */
    public void finish(List<T> data) {
        mPaginationDelegate.finish(data);
    }

    /**
     * 数据请求结束
     * @param data 数据
     */
    public void finishWithResponse(ResponseData<List<T>> data) {
        mPaginationDelegate.finishWithResponse(data);
    }

    /**
     * 数据请求结束
     * @param throwable 异常
     */
    public void finishWithException(Throwable throwable) {
        mPaginationDelegate.finishWithException(throwable);
    }
}
