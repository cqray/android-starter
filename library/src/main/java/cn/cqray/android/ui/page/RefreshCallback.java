package cn.cqray.android.ui.page;

/**
 * 刷新回调
 * @author Cqray
 */
public interface RefreshCallback<T> {

    /**
     * 刷新监听
     * @param delegate 委托
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    void onRefresh(PaginationDelegate<T> delegate, int pageNum, int pageSize);
}
