package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import cn.cqray.android.R;

/**
 * 加载控件
 * @author Cqray
 */
public class LoadingView2 extends SurfaceView {

    private Paint mPaint;
    private RectF mArcRectF;
    private int mViewWidth;
    private int mViewHeight;
    /** 单页最小角度 **/
    private float mMinAngle;
    /** 单页最小角度 **/
    private float mAddAngle;
    /** 旋转速度 **/
    private float mRotateRate;
    /** 抖动比例 **/
    private float mSnakeRatio;
    /** 圆弧宽度 **/
    private float mStrokeWidth;
    /** 叶型圆弧角度间隔 **/
    private float mIntervalAngle;
    /** 开始画弧线的角度 **/
    private float mStartAngle;
    /** 需要画弧线的角度 **/
    private float mSweepAngle;
    /** 是否正在增加角度 **/
    private boolean mAngleAdding;
    /** 叶数 **/
    private int mArcCount;
    /** 圆弧颜色 **/
    private int[] mArcColors;

    private SurfaceHolder mHolder;
    private Future<?> mFuture;

    /** 线程池 **/
    private ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    });

    public LoadingView2(Context context) {
        super(context);
        init(context);
    }

    public LoadingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setZOrderOnTop(true);
        mHolder = getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mFuture = mExecutor.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        draw();
                    }
                }, 0, 15, TimeUnit.MILLISECONDS);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mFuture != null && !mFuture.isCancelled()) {
                    mFuture.cancel(false);
                }
            }
        });
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mArcRectF = new RectF();
        mArcCount = 1;
        mArcColors = new int[] {ContextCompat.getColor(context, R.color.colorAccent)};
        mMinAngle = 30;
        mAddAngle = 270;
        mRotateRate = 4;
        mAngleAdding = true;
        mIntervalAngle = 50;
        mStrokeWidth = (float) Math.ceil(3 * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public LoadingView2 setArcColors(@ColorInt int... colors) {
        if (colors.length == 0) {
            this.mArcColors = new int[] {ContextCompat.getColor(getContext(), R.color.colorAccent)};
        } else {
            this.mArcColors = colors;
            if (colors.length > mArcCount) {
                mArcCount = colors.length;
            }
        }
        return this;
    }

    public LoadingView2 setArcCount(@IntRange(from = 1) int count) {
        mArcCount = count > 1 ? count : 1;
        return this;
    }

    /**
     * 设置增量角度（仅对叶数为1有效）
     * @param add 增量角度
     */
    public LoadingView2 setArcAddAngle(float add) {
        mAddAngle = add;
        return this;
    }

    /**
     * 设置最小角度（仅对叶数为1有效）
     * @param min 最小角度
     */
    public LoadingView2 setArcMinAngle(float min) {
        mMinAngle = min;
        return this;
    }

    public LoadingView2 setArcIntervalAngle(float angle) {
        mIntervalAngle = angle;
        return this;
    }

    /**
     * 设置抖动比例(即改变弧的宽度)
     * @param ratio 抖动比例
     */
    public LoadingView2 setArcShakeRatio(float ratio) {
        mSnakeRatio = ratio;
        return this;
    }

    public LoadingView2 setArcStrokeWidth(float width) {
        mStrokeWidth = getResources().getDisplayMetrics().density * width;
        return this;
    }

    /**
     * 设置转一圈需要时间
     * @param time 转一圈需要时间
     */
    public LoadingView2 setRoundUseTime(int time) {
        mRotateRate = 3600f / time;
        return this;
    }

    private void draw() {
        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        rotate(mArcCount);
        for (int i = 0; i < mArcCount; i++) {
            int squareLength = Math.min(mViewWidth, mViewHeight);
            float strokeWidth = getRealStrokeWidth(squareLength, mArcCount);
            mArcRectF.set(
                    (Math.abs(squareLength - mViewWidth) >> 1) + strokeWidth,
                    (Math.abs(squareLength - mViewHeight) >> 1) + strokeWidth,
                    (Math.abs(squareLength - mViewWidth) >> 1) + squareLength - strokeWidth,
                    (Math.abs(squareLength - mViewHeight) >> 1) + squareLength - strokeWidth);
            mPaint.setColor(mArcColors[i % mArcColors.length]);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);
            float startAngle = mStartAngle + i * (360f / mArcCount);

            // 画圆弧
            canvas.drawArc(mArcRectF, startAngle, mSweepAngle, false, mPaint);
            // 画两个圆圈,是弧线变得圆润
            mPaint.setStyle(Paint.Style.FILL);


            float radius = mArcRectF.width() / 2;
            float endAngle = startAngle + mSweepAngle;


            float x = (float) (mArcRectF.left + radius + radius * Math.cos(startAngle * Math.PI / 180));
            float y = (float) (mArcRectF.top + radius + radius * Math.sin(startAngle * Math.PI / 180));

            float x2 = (float) (mArcRectF.left + radius + radius * Math.cos(endAngle * Math.PI / 180));
            float y2 = (float) (mArcRectF.top + radius + radius * Math.sin(endAngle * Math.PI / 180));


            canvas.drawCircle(x, y, strokeWidth / 2, mPaint);
            canvas.drawCircle(x2, y2, strokeWidth / 2, mPaint);
        }
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void rotate(int arcCount) {
        // 开始的幅度角度
        mStartAngle += mAngleAdding ? mRotateRate : mRotateRate * 2;
        if (arcCount > 1) {
            mSweepAngle = (360f / arcCount - mIntervalAngle);
            mSweepAngle = (int) (mSweepAngle - mSweepAngle / 2 * getRouteNumber());
        } else {
            // 需要画的弧度
            mSweepAngle += mAngleAdding ? mRotateRate : -mRotateRate;
            mAngleAdding = mAngleAdding ? mSweepAngle < mMinAngle + mAddAngle : mSweepAngle <= mMinAngle;
        }
    }

    private float getRealStrokeWidth(int squareLength, int arcCount) {
        return getRouteNumber() * squareLength / 2 * (arcCount > 1 ? mSnakeRatio : 0) + mStrokeWidth;
    }

    private float getRouteNumber() {
        return (float) ((1.0 + Math.sin(Math.PI * mStartAngle / 180))) / 2;
    }
    
}
