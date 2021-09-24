package cn.cqray.android.exception;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.cqray.android.R;
import cn.cqray.android.app.SupportActivity;

/**
 * 异常界面
 * @author Cqray
 */
public class ExceptionActivity extends SupportActivity {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.starter_exception_layout);
        //mToolbar.setTitle("异常");

        Exception e = (Exception) getIntent().getSerializableExtra("exception");
        TextView tv = findViewById(R.id.tv);
        tv.setText(e == null ? null : e.getMessage());
    }
}
