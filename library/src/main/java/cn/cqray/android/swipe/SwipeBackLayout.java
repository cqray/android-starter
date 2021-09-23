package cn.cqray.android.swipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.R;
import cn.cqray.android.app.StarterProvider;

/**
 * 滑动返回控件
 * @author Cqray
 */
public class SwipeBackLayout extends FrameLayout {

    /** 拖拽限制状态 **/
    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;
    /** 拖拽中状态 **/
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;
    /** 拖拽后自然沉降的状态 **/
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;
    /** 拖拽结束状态 **/
    public static final int STATE_FINISHED = 3;
    /** 越界距离 **/
    private static final int OVER_SCROLL_DISTANCE = 10;
    /** 临时矩形 **/
    private final Rect mTmpRect = new Rect();
    /** 是否在onLayout状态 **/
    private boolean mInLayout;
    private int mContentLeft;
    private int mContentTop;
    /** 遮罩颜色 **/
    private int mScrimColor = 0x99000000;
    private float mScrimOpacity;
    private float mScrimAlpha = 0.5f;
    /** 边缘滑动标识 **/
    private int mEdgeFlag;
    /** 是否支持边缘滑动 **/
    private boolean mSwipeBackEnable = true;
    /** 当前边缘滑动方向 **/
    private EdgeOrientation mCurrentOrientation;
    /** 上级视图偏移百分比 **/
    private float mOffsetPercent = 0.33f;
    /** 当前滑动百分比 **/
    private float mScrollPercent = 0.00f;
    /** 边缘滑动关闭百分比 **/
    private float mClosePercent = 0.33f;
    /** 监听事件 **/
    private List<OnSwipeListener> mListeners;
    /** 阴影图片，左、上、右、下**/
    private Drawable [] mShadows = new Drawable[4];
    /** 内容布局 **/
    private View mContentView;
    /** 上个Fragment **/
    private Fragment mPreFragment;
    /** 滑动拖拽辅助类 **/
    private ViewDragHelper mHelper;
    /** Activity **/
    private StarterProvider mActivityProvider;
    /** Fragment **/
    private StarterProvider mFragmentProvider;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = ViewDragHelper.create(this, new ViewDragCallback());
        setEdgeOrientation(EdgeOrientation.LEFT);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean isDrawView = child == mContentView;
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        if (isDrawView && mScrimOpacity > 0 && mHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }
        return drawChild;
    }

    private void drawShadow(Canvas canvas, @NonNull View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);
        if (mCurrentOrientation == EdgeOrientation.LEFT) {
            mShadows[0].setBounds(childRect.left - mShadows[0].getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
            mShadows[0].setAlpha((int) (mScrimOpacity * 255));
            mShadows[0].draw(canvas);
        } else if (mCurrentOrientation == EdgeOrientation.TOP) {
            mShadows[1].setBounds(childRect.left, childRect.top - mShadows[1].getIntrinsicHeight(), childRect.right, childRect.top);
            mShadows[1].setAlpha((int) (mScrimOpacity * 255));
            mShadows[1].draw(canvas);
        } else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
            mShadows[2].setBounds(childRect.right, childRect.top, childRect.right + mShadows[2].getIntrinsicWidth(), childRect.bottom);
            mShadows[2].setAlpha((int) (mScrimOpacity * 255));
            mShadows[2].draw(canvas);
        } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
            mShadows[3].setBounds(childRect.left, childRect.bottom, childRect.right, childRect.bottom + mShadows[3].getIntrinsicHeight());
            mShadows[3].setAlpha((int) (mScrimOpacity * 255));
            mShadows[3].draw(canvas);
        }
    }

    private void drawScrim(Canvas canvas, @NonNull View child) {
        final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity * mScrimAlpha);
        final int color = alpha << 24 | (mScrimColor & 0xffffff);

        if (mCurrentOrientation == EdgeOrientation.LEFT) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else if (mCurrentOrientation == EdgeOrientation.TOP) {
            canvas.clipRect(0, child.getTop(), getWidth(), getTop());
        } else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
            canvas.clipRect(0, child.getBottom(), getWidth(), getBottom());
        }
        canvas.drawColor(color);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInLayout = true;
        if (mContentView != null) {
            mContentView.layout(mContentLeft, mContentTop,
                    mContentLeft + mContentView.getMeasuredWidth(),
                    mContentTop + mContentView.getMeasuredHeight());
        }
        mInLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mScrimOpacity >= 0) {
            if (mHelper.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            // Fragment中上一个SwipeBackLayout变化情况
            if (mPreFragment != null && mPreFragment.getView() != null) {
                Fragment fragment = (Fragment) mFragmentProvider;
                if (fragment.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                    mPreFragment.getView().setX(0);
                    mPreFragment.getView().setY(0);
                    return;
                }

                if (mHelper.getCapturedView() != null) {
                    int distanceX = Math.abs(mHelper.getCapturedView().getLeft() - getWidth());
                    int distanceY = Math.abs(mHelper.getCapturedView().getTop() - getHeight());
                    if (mCurrentOrientation == EdgeOrientation.LEFT) {
                        int offset = (int) (distanceX * mOffsetPercent * mScrimOpacity);
                        mPreFragment.getView().setX(Math.min(-offset, 0));
                    } else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
                        int offset = (int) (distanceX * mOffsetPercent * mScrimOpacity);
                        mPreFragment.getView().setX(Math.max(offset, 0));
                    } else if (mCurrentOrientation == EdgeOrientation.TOP) {
                        int offset = (int) (distanceY * mOffsetPercent * mScrimOpacity);
                        mPreFragment.getView().setY(Math.min(-offset, 0));
                    } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
                        int offset = (int) (distanceY * mOffsetPercent * mScrimOpacity);
                        mPreFragment.getView().setY(Math.max(offset, 0));
                    }
                    // 防止滑动了之后上级Fragment不归位
                    if (mHelper.getCapturedView().getLeft() == 0) {
                        mPreFragment.getView().setX(0);
                        mPreFragment.getView().setY(0);
                    }
                }
            }
            // Activity中上一个SwipeBackLayout变化情况
            SwipeBackDelegate page = SwipeBackHelper.getPreSwipeDelegate(mActivityProvider);
            if (mActivityProvider != null && page != null) {
                if (mHelper.getCapturedView() != null) {
                    if (page.getSwipeBackLayout() != null) {
                        int distanceX = Math.abs(mHelper.getCapturedView().getLeft() - getWidth());
                        int distanceY = Math.abs(mHelper.getCapturedView().getTop() - getHeight());
                        if (mCurrentOrientation == EdgeOrientation.LEFT) {
                            int offset = (int) (distanceX * mOffsetPercent * mScrimOpacity);
                            page.getSwipeBackLayout().setX(Math.min(-offset, 0));
                        } else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
                            int offset = (int) (distanceX * mOffsetPercent * mScrimOpacity);
                            page.getSwipeBackLayout().setX(Math.max(offset, 0));
                        } else if (mCurrentOrientation == EdgeOrientation.TOP) {
                            int offset = (int) (distanceY * mOffsetPercent * mScrimOpacity);
                            page.getSwipeBackLayout().setY(Math.min(-offset, 0));
                        } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
                            int offset = (int) (distanceY * mOffsetPercent * mScrimOpacity);
                            page.getSwipeBackLayout().setY(Math.max(offset, 0));
                        }
                        // 防止滑动了之后上级Activity不归位
                        if (mHelper.getCapturedView().getLeft() == 0) {
                            page.getSwipeBackLayout().setX(0);
                            page.getSwipeBackLayout().setY(0);
                        }
                    }
                }
            }
        }
    }

    private void setContentView(View view) {
        mContentView = view;
    }

    private void validateEdgeLevel(int widthPixel, float percent) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            assert windowManager != null;
            windowManager.getDefaultDisplay().getMetrics(metrics);
            Field mEdgeSize = mHelper.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            if (widthPixel >= 0) {
                mEdgeSize.setInt(mHelper, widthPixel);
            } else {
                float validPercent = Math.abs(percent) > 1 ? 1 : Math.abs(percent);
                mEdgeSize.setInt(mHelper, (int) (metrics.widthPixels * validPercent));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void attachToActivity(StarterProvider provider) {
        if (getParent() == null) {
            mActivityProvider = provider;
            AppCompatActivity act = (AppCompatActivity) provider;
            TypedArray a = act.getTheme().obtainStyledAttributes(new int[]{
                    android.R.attr.windowBackground
            });
            int background = a.getResourceId(0, 0);
            a.recycle();
            View decorChild = act.findViewById(android.R.id.content);
            ViewGroup decor = (ViewGroup) decorChild.getParent();
            decorChild.setBackgroundResource(background);
            decor.removeView(decorChild);
            addView(decorChild);
            setContentView(decorChild);
            decor.addView(this);
        }
    }

    public void attachToFragment(@NonNull StarterProvider fragment, @NonNull View view) {
        if (view.getParent() == null) {
            addView(view);
            mFragmentProvider = fragment;
            mContentView = view;
        }
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackEnable = enable;
    }

    public void setSwipeEdge(int widthPixel) {
        validateEdgeLevel(widthPixel, 0);
    }

    public void setSwipeEdgePercent(@FloatRange(from = 0.0f, to = 1.0f) float percent) {
        validateEdgeLevel(-1, percent);
    }

    /**
     * 滑动中，上一个页面View的遮罩透明度
     * @param alpha 0.0f:无阴影, 1.0f:较重的阴影, 默认:0.5f
     */
    public void setScrimAlpha(@FloatRange(from = 0.0f, to = 1.0f) float alpha) {
        mScrimAlpha = Math.abs(alpha) > 1 ? 1 : Math.abs(alpha);
    }

    /**
     * 滑动中，上一个页面View的遮罩颜色
     * @param color 颜色
     */
    public void setScrimColor(int color) {
        mScrimColor = color;
        invalidate();
    }

    /**
     * 关闭百分比, 滚动多少将关闭界面（百分比）
     * @param percent 关闭百分比（百分比）
     */
    public void setClosePercent(@FloatRange(from = 0.0f, to = 1.0f) float percent) {
        if (percent >= 1 || percent <= 0) {
            throw new IllegalArgumentException("Percent value should be between 0 and 1.0");
        }
        mClosePercent = percent;
    }

    /**
     * 设置偏移百分比（百分比）
     * @param percent 偏移百分比
     */
    public void setOffsetPercent(float percent) {
        mOffsetPercent = percent;
    }

    public void setEdgeOrientation(@NonNull EdgeOrientation orientation) {
        mEdgeFlag = orientation.mCode;
        mHelper.setEdgeTrackingEnabled(orientation.mCode);
        switch (orientation) {
            case LEFT:
                setShadow(R.drawable.swipe_shadow_left, EdgeOrientation.LEFT);
                break;
            case RIGHT:
                setShadow(R.drawable.swipe_shadow_right, EdgeOrientation.RIGHT);
                break;
            case HORIZONTAL:
                setShadow(R.drawable.swipe_shadow_left, EdgeOrientation.LEFT);
                setShadow(R.drawable.swipe_shadow_right, EdgeOrientation.RIGHT);
                break;
            case TOP:
                setShadow(R.drawable.swipe_shadow_top, EdgeOrientation.TOP);
                break;
            case BOTTOM:
                setShadow(R.drawable.swipe_shadow_bottom, EdgeOrientation.BOTTOM);
                break;
            case VERTICAL:
                setShadow(R.drawable.swipe_shadow_top, EdgeOrientation.TOP);
                setShadow(R.drawable.swipe_shadow_bottom, EdgeOrientation.BOTTOM);
                break;
            case ALL:
            default:
                setShadow(R.drawable.swipe_shadow_left, EdgeOrientation.LEFT);
                setShadow(R.drawable.swipe_shadow_right, EdgeOrientation.RIGHT);
                setShadow(R.drawable.swipe_shadow_top, EdgeOrientation.TOP);
                setShadow(R.drawable.swipe_shadow_bottom, EdgeOrientation.BOTTOM);
        }
    }

    public void setShadow(int resId, EdgeOrientation orientation) {
        setShadow(ContextCompat.getDrawable(getContext(), resId), orientation);
    }

    public void setShadow(Drawable shadow, @NonNull EdgeOrientation orientation) {
        switch (orientation) {
            case LEFT:
                mShadows[0] = shadow;
                break;
            case RIGHT:
                mShadows[2] = shadow;
                break;
            case HORIZONTAL:
                mShadows[0] = shadow;
                mShadows[2] = shadow;
                break;
            case TOP:
                mShadows[1] = shadow;
                break;
            case BOTTOM:
                mShadows[3] = shadow;
                break;
            case VERTICAL:
                mShadows[1] = shadow;
                mShadows[3] = shadow;
                break;
            case ALL:
            default:
                mShadows[0] = shadow;
                mShadows[1] = shadow;
                mShadows[2] = shadow;
                mShadows[3] = shadow;
        }
        invalidate();
    }

    public void addOnSwipeListener(OnSwipeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    public void removeOnSwipeListener(OnSwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    public interface OnSwipeListener {
        /**
         * 状态变化
         * @param state flag to describe scroll state
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         * @see #STATE_FINISHED
         */
        void onDragStateChange(int state);

        /**
         * 开始滑动
         * @param orientation 边缘方向
         */
        void onEdgeTouch(int orientation);

        /**
         * 滑动的百分比距离
         * @param percent 百分比距离
         */
        void onScrollPercent(float percent);
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            boolean dragEnable = mHelper.isEdgeTouched(mEdgeFlag, pointerId);
            if (dragEnable) {
                if (mHelper.isEdgeTouched(EdgeOrientation.LEFT.mCode, pointerId)) {
                    mCurrentOrientation = EdgeOrientation.LEFT;
                } else if (mHelper.isEdgeTouched(EdgeOrientation.RIGHT.mCode, pointerId)) {
                    mCurrentOrientation = EdgeOrientation.RIGHT;
                } else if (mHelper.isEdgeTouched(EdgeOrientation.TOP.mCode, pointerId)) {
                    mCurrentOrientation = EdgeOrientation.TOP;
                } else if (mHelper.isEdgeTouched(EdgeOrientation.BOTTOM.mCode, pointerId)) {
                    mCurrentOrientation = EdgeOrientation.BOTTOM;
                }

                if (mListeners != null) {
                    for (OnSwipeListener listener : mListeners) {
                        listener.onEdgeTouch(mCurrentOrientation.mCode);
                    }
                }

                if (mPreFragment == null) {
                    if (mFragmentProvider != null) {
                        List<Fragment> fragmentList = ((Fragment) mFragmentProvider).getFragmentManager().getFragments();
                        if (fragmentList.size() > 1) {
                            int index = fragmentList.indexOf((Fragment) mFragmentProvider);
                            for (int i = index - 1; i >= 0; i--) {
                                Fragment fragment = fragmentList.get(i);
                                if (fragment != null && fragment.getView() != null) {
                                    mPreFragment = fragment;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return dragEnable;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int ret = 0;
            if (mCurrentOrientation == EdgeOrientation.LEFT) {
                ret = Math.min(child.getWidth(), Math.max(left, 0));
            } else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
                ret = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return ret;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int ret = 0;
            if (mCurrentOrientation == EdgeOrientation.TOP) {
                ret = Math.min(child.getHeight(), Math.max(top, 0));
            } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
                ret = Math.min(0, Math.max(top, -child.getHeight()));
            }
            return ret;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (mCurrentOrientation == EdgeOrientation.LEFT) {
                mScrollPercent = Math.abs((float) left / (mContentView.getWidth() + mShadows[0].getIntrinsicWidth()));
            } else if (mCurrentOrientation == EdgeOrientation.TOP) {
                mScrollPercent = Math.abs((float) top / (mContentView.getHeight() + mShadows[1].getIntrinsicHeight()));
            } else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
                mScrollPercent = Math.abs((float) left / (mContentView.getWidth() + mShadows[2].getIntrinsicWidth()));
            } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
                mScrollPercent = Math.abs((float) top / (mContentView.getHeight() + mShadows[3].getIntrinsicHeight()));
            }
            mContentLeft = left;
            mContentTop = top;
            invalidate();

            // 回调滑动滚动百分比
            if (mListeners != null
                    && mHelper.getViewDragState() == STATE_DRAGGING
                    && mScrollPercent <= 1
                    && mScrollPercent > 0) {
                for (OnSwipeListener listener : mListeners) {
                    listener.onScrollPercent(mScrollPercent);
                }
            }
            // 滑动滚动大于1，表示滚动完毕
            if (mScrollPercent > 1) {
                if (mFragmentProvider != null) {
                    Fragment fragment = (Fragment) mFragmentProvider;
                    if (fragment.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                        return;
                    }
                    if (!((Fragment) mFragmentProvider).isStateSaved()) {
                        onDragFinished();
                        mFragmentProvider.getStarterDelegate().pop();
                    }
                } else if (mActivityProvider != null) {
                    AppCompatActivity act = (AppCompatActivity) mActivityProvider;
                    if (!act.isFinishing()) {
                        onDragFinished();
                        act.finish();
                        act.overridePendingTransition(0, 0);
                    }
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            if (mFragmentProvider != null) {
                return 1;
            }
            if (mActivityProvider != null) {
                return mActivityProvider.getStarterDelegate().canPop() ? 0 : 1;
            }
            return 1;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
            final int childWidth = releasedChild.getWidth();
            final int childHeight = releasedChild.getHeight();
            int left = 0;
            int top = 0;
            if (mCurrentOrientation == EdgeOrientation.LEFT) {
                left = xVel > 0 || xVel == 0 && mScrollPercent > mClosePercent ? (childWidth
                        + mShadows[0].getIntrinsicWidth() + OVER_SCROLL_DISTANCE) : 0;
            } else if (mCurrentOrientation == EdgeOrientation.TOP) {
                top = yVel > 0 || yVel == 0 && mScrollPercent > mClosePercent ? (childHeight
                        + mShadows[1].getIntrinsicHeight() + OVER_SCROLL_DISTANCE) : 0;
            }  else if (mCurrentOrientation == EdgeOrientation.RIGHT) {
                left = xVel < 0 || xVel == 0 && mScrollPercent > mClosePercent ? -(childWidth
                        + mShadows[2].getIntrinsicWidth() + OVER_SCROLL_DISTANCE) : 0;
            } else if (mCurrentOrientation == EdgeOrientation.BOTTOM) {
                top = yVel < 0 || yVel == 0 && mScrollPercent > mClosePercent ? -(childHeight
                        + mShadows[3].getIntrinsicHeight() + OVER_SCROLL_DISTANCE) : 0;
            }
            mHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListeners != null) {
                for (OnSwipeListener listener : mListeners) {
                    listener.onDragStateChange(state);
                }
            }
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            if ((mEdgeFlag & edgeFlags) != 0) {
                mCurrentOrientation = EdgeOrientation.find(edgeFlags);
            }
        }
    }

    private void onDragFinished() {
        if (mListeners != null) {
            for (OnSwipeListener listener : mListeners) {
                listener.onDragStateChange(STATE_FINISHED);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mSwipeBackEnable) {
            return super.onInterceptTouchEvent(ev);
        }
        try {
            return mHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSwipeBackEnable) {
            return true;
        }
        try {
            mHelper.processTouchEvent(event);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
