package cn.cqray.demo.starter;

import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.anim.DefaultHorizontalAnimator;

/**
 * @author Admin
 * @date 2021/9/23 10:51
 */
public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Starter.getInstance().initialize(this);

        StarterStrategy strategy = StarterStrategy.builder()
                .fragmentAnimator(new DefaultHorizontalAnimator())
                .fragmentDrawable(new ColorDrawable(Color.WHITE))
                .build();
    }
}
