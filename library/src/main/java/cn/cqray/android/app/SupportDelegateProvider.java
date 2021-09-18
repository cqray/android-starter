package cn.cqray.android.app;

import cn.cqray.android.anim.FragmentAnimator;

/**
 * 功能实现
 * @author Cqray
 */
public interface SupportDelegateProvider {

    SupportDelegate getSupportDelegate();

    FragmentAnimator onCreateFragmentAnimator();

    boolean onBackPressedSupport();
}
