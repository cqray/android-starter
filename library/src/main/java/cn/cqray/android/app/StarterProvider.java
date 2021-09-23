package cn.cqray.android.app;

import cn.cqray.android.anim.FragmentAnimator;

/**
 * 功能实现
 * @author Cqray
 */
public interface StarterProvider {

    StarterDelegate getStarterDelegate();

    FragmentAnimator onCreateFragmentAnimator();

    boolean onBackPressedSupport();
}
