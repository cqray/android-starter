package cn.cqray.android.app;

import cn.cqray.android.anim.FragmentAnimator;

/**
 * 功能实现
 * @author Cqray
 */
public interface SupportProvider {

    /**
     * 获取委托实例
     * @return 委托实例
     */
    SupportDelegate getSupportDelegate();

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
     * 进入动画结束
     */
    default void onEnterAnimEnd() {}
}
