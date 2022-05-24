package cn.cqray.android.util;

import java.util.List;

public class TraverseUtils {

    public static <T> void iterable(Iterable<T> iterable, Callback<T> callback) {
        if (iterable != null) {
            for (T t : iterable) {
                if (callback != null && callback.onCall(t)) {
                    break;
                }
            }
        }
    }

    public static <T> void list(List<T> list, Callback<T> callback) {
        iterable(list, callback);
    }

    /**
     * 遍历回调
     */
    public interface Callback<T> {

        /**
         * 遍历子项回调
         * @param item 子项
         * @return 是否拦截
         */
        boolean onCall(T item);
    }
}
