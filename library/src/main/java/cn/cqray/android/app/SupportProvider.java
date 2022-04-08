package cn.cqray.android.app;

import cn.cqray.android.anim.FragmentAnimator;

/**
 * 功能实现
 * @author Cqray
 */
public interface SupportProvider {

    SupportDelegate getStarterDelegate();

    /**
     * 创建Fragment动画
     * @return FragmentAnimator实例
     */
    default FragmentAnimator onCreateFragmentAnimator() {
        return null;
    }

    /**
     * 回退事件拦截
     * @return true 拦截 false 不拦截
     */
    default boolean onBackPressedSupport() { return false; }

    /**
     * 键盘自动隐藏
     * @return true 自动隐藏 false 不处理
     */
    default boolean onKeyboardAutoHide() {
        return true;
    }
}
