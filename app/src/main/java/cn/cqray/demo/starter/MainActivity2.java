package cn.cqray.demo.starter;

import android.graphics.Color;
import android.os.Bundle;

import com.blankj.utilcode.util.GsonUtils;

import java.util.List;

import cn.cqray.android.app.NavActivity;
import cn.cqray.android.app.SupportActivity;
import cn.cqray.android.extra.ResponseData;

public class MainActivity2 extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        mToolbar.setBackgroundColor(Color.RED);
    }

}