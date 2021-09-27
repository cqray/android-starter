package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.SupportFragment;

/**
 * @author Admin
 * @date 2021/9/23 17:31
 */
public class MainFragment2 extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mToolbar.setElevation(30);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("数据", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("数据", "onPause");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(new NavIntent(MainFragment2.class));
            }
        });
    }
}
