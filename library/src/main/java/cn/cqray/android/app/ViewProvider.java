package cn.cqray.android.app;

/**
 * 布局提供者
 * @author Cqray
 * @date 2022/3/13
 */
public interface ViewProvider {

    ViewDelegate getViewDelegate();

    default boolean onSupportKeyboardAutoHide() {
        return true;
    }

    default boolean onSupportSwipeBack() {
        return false;
    }
}
