package cn.cqray.android.tip;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;

import java.util.List;

import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;

/**
 * 提示消息展示委托
 * <p>可以在任意线程中调用</p>
 * @author Cqray
 * @date 2022/3/12
 */
public class TipDelegate {

    /** 时间间隔 **/
    private static final int DURATION = 1500;
    /** 适配器 **/
    private TipAdapter mTipAdapter;
    /** 标签 **/
    private final Object mTag;

    public TipDelegate() {
        mTag = null;
    }

    public TipDelegate(@Nullable Object tag) {
        mTag = tag;
    }

    public synchronized void setAdapter(TipAdapter adapter) {
        mTipAdapter = adapter;
    }

    public void showInfo(String text) {
        show(TipLevel.INFO, text, DURATION);
    }

    public void showInfo(String text, int duration) {
        show(TipLevel.INFO, text, duration);
    }

    public void showWarning(String text) {
        show(TipLevel.WARNING, text, DURATION);
    }

    public void showWarning(String text, int duration) {
        show(TipLevel.WARNING, text, duration);
    }

    public void showError(String text) {
        show(TipLevel.ERROR, text, DURATION);
    }

    public void showError(String text, int duration) {
        show(TipLevel.ERROR, text, duration);
    }

    public void showSuccess(String text) {
        show(TipLevel.SUCCESS, text, DURATION);
    }

    public void showSuccess(String text, int duration) {
        show(TipLevel.SUCCESS, text, duration);
    }

    /**
     * 显示提示
     * @param level 提示等级
     * @param text 提示内容
     * @param duration 提示时长
     */
    private void show(TipLevel level, String text, int duration) {
        List<Activity> activities = ActivityUtils.getActivityList();
        if (activities.isEmpty()) {
            return;
        }
        Activity act = activities.get(0);
        act.runOnUiThread(() -> {
            synchronized (TipDelegate.class) {
                if (mTipAdapter == null) {
                    StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
                    mTipAdapter = strategy.getTipAdapter();
                }
                if (mTipAdapter == null) {
                    mTipAdapter = new TipAdapterImpl();
                }
                // 显示提示信息
                mTipAdapter.show(mTag, level, text, duration);
            }
        });
    }
}