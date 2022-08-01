package cn.cqray.demo.starter;

import android.graphics.Color;
import android.os.Bundle;

import cn.cqray.android.app.SupportActivity;

public class MainActivity2 extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        mToolbar.setBackgroundColor(Color.RED);
    }

}