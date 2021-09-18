package cn.cqray.android.anim;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;

/**
 * Fragment动画
 * @author Cqray
 */
public class FragmentAnimator {

    /** A跳转B时，B的动画 **/
    @AnimRes
    @AnimatorRes
    public int mEnter;
    /** A跳转B时，A的动画 **/
    @AnimRes
    @AnimatorRes
    public int mExit;
    /** A跳转B后，后退到A，A的动画 **/
    @AnimRes
    @AnimatorRes
    public int mPopEnter;
    /** A跳转B后，后退到A，B的动画 **/
    @AnimRes
    @AnimatorRes
    public int mPopExit;

    /**
     * @param enter A跳转B时，B的动画
     * @param exit A跳转B时，A的动画
     * @param popEnter A跳转B后，后退到A，A的动画
     * @param popExit A跳转B后，后退到A，B的动画
     */
    public FragmentAnimator(@AnimRes @AnimatorRes int enter,
                            @AnimRes @AnimatorRes int exit,
                            @AnimRes @AnimatorRes int popEnter,
                            @AnimRes @AnimatorRes int popExit) {
        mEnter = enter;
        mExit = exit;
        mPopEnter = popEnter;
        mPopExit = popExit;
    }
}
