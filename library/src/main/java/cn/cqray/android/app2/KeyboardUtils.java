package cn.cqray.android.app2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/02
 *     desc  : utils about keyboard
 * </pre>
 */
public final class KeyboardUtils {

//    private static final int TAG_ON_GLOBAL_LAYOUT_LISTENER = -8;
//
//    private KeyboardUtils() {
//        throw new UnsupportedOperationException("u can't instantiate me...");
//    }
//
//    /**
//     * 显示软键盘
//     */
//    public static void showSoftInput() {
//        InputMethodManager imm = (InputMethodManager) Get.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm == null) {
//            return;
//        }
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }
//
//    /**
//     * 显示软键盘
//     */
//    public static void showSoftInput(@Nullable Activity activity) {
//        if (activity == null) {
//            return;
//        }
//        if (!isSoftInputVisible(activity)) {
//            toggleSoftInput();
//        }
//    }
//
//    /**
//     * 显示软键盘
//     * @param view The view.
//     */
//    public static void showSoftInput(@NonNull final View view) {
//        showSoftInput(view, 0);
//    }
//
//    /**
//     * 显示软键盘
//     * @param view The view
//     * @param flags Provides additional operating flags.  Currently may be
//     * 0 or have the {@link InputMethodManager#SHOW_IMPLICIT} bit set.
//     */
//    public static void showSoftInput(@NonNull final View view, final int flags) {
//        InputMethodManager imm = (InputMethodManager) Get.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm == null) {
//            return;
//        }
//        view.setFocusable(true);
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        imm.showSoftInput(view, flags, new ResultReceiver(new Handler()) {
//            @Override
//            protected void onReceiveResult(int resultCode, Bundle resultData) {
//                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
//                        || resultCode == InputMethodManager.RESULT_HIDDEN) {
//                    toggleSoftInput();
//                }
//            }
//        });
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }
//
//    /**
//     * 隐藏软键盘
//     * @param activity The activity.
//     */
//    public static void hideSoftInput(final Activity activity) {
//        if (activity == null) {
//            return;
//        }
//        hideSoftInput(activity.getWindow());
//    }
//
//    /**
//     * 隐藏软键盘
//     * @param window The window.
//     */
//    public static void hideSoftInput(final Window window) {
//        if (window == null) {
//            return;
//        }
//        View view = window.getCurrentFocus();
//        if (view == null) {
//            View decorView = window.getDecorView();
//            View focusView = decorView.findViewWithTag("keyboardTagView");
//            if (focusView == null) {
//                view = new EditText(window.getContext());
//                view.setTag("keyboardTagView");
//                ((ViewGroup) decorView).addView(view, 0, 0);
//            } else {
//                view = focusView;
//            }
//            view.requestFocus();
//        }
//        hideSoftInput(view);
//    }
//
//    /**
//     * 隐藏软键盘
//     * @param view The view.
//     */
//    public static void hideSoftInput(final View view) {
//        InputMethodManager imm = (InputMethodManager) Get.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm == null || view == null) {
//            return;
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
//
//    /**
//     * 切换软键盘状态
//     */
//    public static void toggleSoftInput() {
//        InputMethodManager imm = (InputMethodManager) Get.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm == null) {
//            return;
//        }
//        imm.toggleSoftInput(0, 0);
//    }
//
//    private static int sDecorViewDelta = 0;
//
//    /**
//     * Return whether soft input is visible.
//     *
//     * @param activity The activity.
//     * @return {@code true}: yes<br>{@code false}: no
//     */
//    public static boolean isSoftInputVisible(@NonNull final Activity activity) {
//        return getDecorViewInvisibleHeight(activity.getWindow()) > 0;
//    }
//
//    /**
//     * 获取DecorView未显示的高度
//     */
//    private static int getDecorViewInvisibleHeight(@NonNull final Window window) {
//        final View decorView = window.getDecorView();
//        final Rect outRect = new Rect();
//        decorView.getWindowVisibleDisplayFrame(outRect);
//        Log.d("KeyboardUtils", "getDecorViewInvisibleHeight: " + (decorView.getBottom() - outRect.bottom));
//        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
//        if (delta <= getNavBarHeight() + getStatusBarHeight()) {
//            sDecorViewDelta = delta;
//            return 0;
//        }
//        return delta - sDecorViewDelta;
//    }
//
//    /**
//     * Register soft input changed listener.
//     *
//     * @param activity The activity.
//     * @param listener The soft input changed listener.
//     */
//    public static void registerSoftInputChangedListener(@NonNull Activity activity,
//                                                        @NonNull OnSoftInputChangedListener listener) {
//        registerSoftInputChangedListener(activity.getWindow(), listener);
//    }
//
//    /**
//     * Register soft input changed listener.
//     *
//     * @param window The window.
//     * @param listener The soft input changed listener.
//     */
//    public static void registerSoftInputChangedListener(@NonNull final Window window,
//                                                        @NonNull final OnSoftInputChangedListener listener) {
//        final int flags = window.getAttributes().flags;
//        if ((flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//        final FrameLayout contentView = window.findViewById(android.R.id.content);
//        final int[] decorViewInvisibleHeightPre = { getDecorViewInvisibleHeight(window) };
//        OnGlobalLayoutListener onGlobalLayoutListener = () -> {
//            int height = getDecorViewInvisibleHeight(window);
//            if (decorViewInvisibleHeightPre[0] != height) {
//                listener.onSoftInputChanged(height);
//                decorViewInvisibleHeightPre[0] = height;
//            }
//        };
//        contentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
//        contentView.setTag(TAG_ON_GLOBAL_LAYOUT_LISTENER, onGlobalLayoutListener);
//    }
//
//    /**
//     * Unregister soft input changed listener.
//     *
//     * @param window The window.
//     */
//    public static void unregisterSoftInputChangedListener(@NonNull final Window window) {
//        final View contentView = window.findViewById(android.R.id.content);
//        if (contentView == null) {
//            return;
//        }
//        Object tag = contentView.getTag(TAG_ON_GLOBAL_LAYOUT_LISTENER);
//        if (tag instanceof OnGlobalLayoutListener) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                contentView.getViewTreeObserver().removeOnGlobalLayoutListener((OnGlobalLayoutListener) tag);
//                //这里会发生内存泄漏 如果不设置为null
//                contentView.setTag(TAG_ON_GLOBAL_LAYOUT_LISTENER, null);
//            }
//        }
//    }
//
//    /**
//     * Fix the bug of 5497 in Android.
//     * <p>Don't set adjustResize</p>
//     *
//     * @param activity The activity.
//     */
//    public static void fixAndroidBug5497(@NonNull final Activity activity) {
//        fixAndroidBug5497(activity.getWindow());
//    }
//
//    /**
//     * Fix the bug of 5497 in Android.
//     * <p>It will clean the adjustResize</p>
//     *
//     * @param window The window.
//     */
//    public static void fixAndroidBug5497(@NonNull final Window window) {
//        int softInputMode = window.getAttributes().softInputMode;
//        window.setSoftInputMode(softInputMode & ~WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        final FrameLayout contentView = window.findViewById(android.R.id.content);
//        final View contentViewChild = contentView.getChildAt(0);
//        final int paddingBottom = contentViewChild.getPaddingBottom();
//        final int[] contentViewInvisibleHeightPre5497 = { getContentViewInvisibleHeight(window) };
//        contentView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            int height = getContentViewInvisibleHeight(window);
//            if (contentViewInvisibleHeightPre5497[0] != height) {
//                contentViewChild.setPadding(
//                        contentViewChild.getPaddingLeft(),
//                        contentViewChild.getPaddingTop(),
//                        contentViewChild.getPaddingRight(),
//                        paddingBottom + getDecorViewInvisibleHeight(window));
//                contentViewInvisibleHeightPre5497[0] = height;
//            }
//        });
//    }
//
//    private static int getContentViewInvisibleHeight(@NonNull final Window window) {
//        final View contentView = window.findViewById(android.R.id.content);
//        if (contentView == null) {
//            return 0;
//        }
//        final Rect outRect = new Rect();
//        contentView.getWindowVisibleDisplayFrame(outRect);
//        Log.d("KeyboardUtils", "getContentViewInvisibleHeight: " + (contentView.getBottom() - outRect.bottom));
//        int delta = Math.abs(contentView.getBottom() - outRect.bottom);
//        if (delta <= getStatusBarHeight() + getNavBarHeight()) {
//            return 0;
//        }
//        return delta;
//    }
//
//    /**
//     * 修复软键盘输入泄漏问题
//     * @param activity The activity.
//     */
//    public static void fixSoftInputLeaks(@NonNull final Activity activity) {
//        fixSoftInputLeaks(activity.getWindow());
//    }
//
//    /**
//     * 修复软键盘输入泄漏问题
//     * @param window The window.
//     */
//    public static void fixSoftInputLeaks(@NonNull final Window window) {
//        InputMethodManager imm = (InputMethodManager) Get.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm == null) {
//            return;
//        }
//        String[] leakViews = new String[] { "mLastSrvView", "mCurRootView", "mServedView", "mNextServedView" };
//        for (String leakView : leakViews) {
//            try {
//                Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
//                if (!leakViewField.isAccessible()) {
//                    leakViewField.setAccessible(true);
//                }
//                Object obj = leakViewField.get(imm);
//                if (!(obj instanceof View)) {
//                    continue;
//                }
//                View view = (View) obj;
//                if (view.getRootView() == window.getDecorView().getRootView()) {
//                    leakViewField.set(imm, null);
//                }
//            } catch (Throwable ignore) {/**/}
//        }
//    }
//
//    private static int getStatusBarHeight() {
//        Resources resources = Get.getContext().getResources();
//        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
//        return resources.getDimensionPixelSize(resourceId);
//    }
//
//    private static int getNavBarHeight() {
//        Resources res = Get.getContext().getResources();
//        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
//        if (resourceId != 0) {
//            return res.getDimensionPixelSize(resourceId);
//        } else {
//            return 0;
//        }
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    // interface
//    ///////////////////////////////////////////////////////////////////////////
//    public interface OnSoftInputChangedListener {
//        void onSoftInputChanged(int height);
//    }
}

