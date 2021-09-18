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
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout._core_lauout_exception);
        mToolbar.setTitle("异常");

        Exception e = (Exception) getIntent().getSerializableExtra("exception");
        TextView tv = findViewById(R.id.tv);
        tv.setText(e == null ? null : e.getMessage());
    }
}
