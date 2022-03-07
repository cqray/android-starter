package cn.cqray.android.state;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.cqray.android.R;

/**
 * 空布局适配器
 * @author Cqray
 */
public class EmptyAdapter extends StateAdapter {

    public EmptyAdapter() {
        super(R.layout.starter_state_empty_layout);
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        ViewGroup parent = (ViewGroup) view;
        TextView btn = (TextView) parent.getChildAt(2);
        btn.setOnClickListener(v -> getRefreshLayout().setBusy());
        setButtonVisible(false);
        setText(getText());
    }

    public void setButtonVisible(final boolean visible) {
        post(() -> {
            ViewGroup parent = (ViewGroup) getView();
            TextView btn = (TextView) parent.getChildAt(2);
            btn.setVisibility(visible ? View.VISIBLE : View.GONE);
        });
    }


    @Override
    protected void show(String text) {
        super.show(text);
        setText(text);
    }

    protected void setText(final String text) {
        post(() -> {
            ViewGroup parent = (ViewGroup) getView();
            TextView tv = (TextView) parent.getChildAt(1);
            tv.setText(TextUtils.isEmpty(text) ? "暂无数据" : text);
        });
    }
}
