package cn.cqray.demo.starter;

import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.anim.DefaultHorizontalAnimator;
import cn.cqray.android.app.ToastDelegate;
import cn.cqray.android.state.BusyAdapter;
import cn.cqray.android.state.EmptyAdapter;
import cn.cqray.android.state.ErrorAdapter;

/**
 * @author Admin
 * @date 2021/9/23 10:51
 */
public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        StarterStrategy strategy = StarterStrategy.builder()
                .fragmentAnimator(new DefaultHorizontalAnimator())
                //.fragmentBackground(new ColorDrawable(Color.RED))
                //.toolbarTitleTextColor(Color.WHITE)
                //.toolbarTitleTextSize(14)
                .fragmentBackgroundRes(R.color.background)
                .busyAdapter(new BusyAdapter())
                .emptyAdapter(new EmptyAdapter())
                .errorAdapter(new ErrorAdapter())
                .build();
        Starter.getInstance().initialize(this, strategy);
    }
}
