package cn.cqray.android.app2;

import cn.cqray.android.anim.FragmentAnimator;

public interface GetProvider {

    GetDelegate getDelegateX();

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

    default void setResult() {}
}
