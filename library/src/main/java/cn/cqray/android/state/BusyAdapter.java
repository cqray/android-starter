package cn.cqray.android.state;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;
import cn.cqray.android.widget.SpinView;

/**
 * 忙碌界面适配器
 * @author Cqray
 */
public class BusyAdapter extends StateAdapter {

    public BusyAdapter() {
        super(R.layout.starter_state_busy_layout);
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        Context context = view.getContext();
        int primary = ContextCompat.getColor(context, R.color.colorPrimary);
        int accent = ContextCompat.getColor(context, R.color.colorAccent);
        ViewGroup parent = (ViewGroup) view;
        SpinView lv = (SpinView) parent.getChildAt(0);
        lv.setArcCount(4);
        lv.setArcShakeRatio(0.12f);
        lv.setArcStrokeWidth(4);
        lv.setArcColors(primary, accent);
    }
}
