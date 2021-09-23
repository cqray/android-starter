package cn.cqray.demo.starter;

import android.app.Application;

import cn.cqray.android.Starter;

/**
 * @author Admin
 * @date 2021/9/23 10:51
 */
public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Starter.getInstance().initialize(this);
    }
}
