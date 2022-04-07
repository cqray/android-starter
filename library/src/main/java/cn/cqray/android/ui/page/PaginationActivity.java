package cn.cqray.android.ui.page;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import cn.cqray.android.R;
import cn.cqray.android.app.SupportActivity;

/**
 * 分页Activity
 * @author Cqray
 */
public abstract class PaginationActivity<T> extends SupportActivity {

    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter<T, ? extends BaseViewHolder> mAdapter;
    protected final PaginationDelegate<T> mPaginationDelegate = new PaginationDelegate<>(this);

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.starter_pagination_layout);
        mRecyclerView = findViewById(R.id.__android_recycler);
        // 初始化适配器
        mAdapter = onCreateAdapter();
        mAdapter.setRecyclerView(mRecyclerView);
        // 初始化列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        // 初始化分页委托
        mPaginationDelegate.setRefreshLayout(mRefreshLayout);
        mPaginationDelegate.setAdapter(mAdapter);
        mPaginationDelegate.setRefreshCallback((delegate, pageNum, pageSize) -> PaginationActivity.this.onRefresh(pageNum, pageSize));
        autoRefresh();
    }

    /**
     * 创建Adapter
     * @return Adapter对象
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
}
