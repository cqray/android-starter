package cn.cqray.android.app;

/**
 * 布局提供者
 * @author Cqray
 * @date 2022/3/13
 */
public interface ViewProvider {



    /**
     * 获取委托实例
     * @return 委托实例
     */
    ViewDelegate getViewDelegate();

    /**
     * 键盘自动隐藏
     * @return true 自动隐藏 false 不处理
     */
    default boolean onKeyboardAutoHide() {
        return true;
    }
}
