package cn.cqray.android.exception;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;

import cn.cqray.android.Starter;

/**
 * 异常管理界面
 * @author Cqray
 */
public class ExceptionManager {

    private static volatile ExceptionManager mInstance;

    private ExceptionManager() {
    }

    public static ExceptionManager getInstance() {
        if (mInstance == null) {
            synchronized (ExceptionManager.class) {
                if (mInstance == null) {
                    mInstance = new ExceptionManager();
                }
            }
        }
        return mInstance;
    }

    public void showException(Exception exc) {
        Context context = ActivityUtils.getTopActivity();
        if (context == null) {
            context = Starter.getInstance().getContext();
        }
        Intent intent = new Intent(context, ExceptionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("exception", exc);
        context.startActivity(intent);
    }
}
