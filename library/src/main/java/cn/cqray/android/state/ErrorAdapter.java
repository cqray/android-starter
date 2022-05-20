package cn.cqray.android.state;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.cqray.android.R;

/**
 * 错误界面适配器
 * @author Cqray
 */
public class ErrorAdapter extends StateAdapter<ErrorAdapter> {

    private View.OnClickListener mRetryListener;

    public ErrorAdapter() {
        super(R.layout.starter_state_error_layout);
        if (getClass().getSuperclass() == StateAdapter.class) {
            makeSureOverridden();
        }
        setDefaultText("暂无数据");
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        ViewGroup parent = (ViewGroup) view;
        TextView btn = (TextView) parent.getChildAt(2);
        btn.setOnClickListener(v -> {
            if (mRetryListener != null) {
                mRetryListener.onClick(v);
            }
        });
    }

    @Override
    protected void onTextChanged(String text) {
        ViewGroup parent = (ViewGroup) getContentView();
        TextView tv = (TextView) parent.getChildAt(1);
        tv.setText(TextUtils.isEmpty(text) ? "页面异常" : text);
    }

    @Override
    protected void onBackgroundChanged(Drawable background) {

    }

    public void setButtonVisible(final boolean visible) {
        ViewGroup parent = (ViewGroup) getContentView();
        TextView btn = (TextView) parent.getChildAt(2);
        btn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setRetryListener(View.OnClickListener listener) {
        mRetryListener = listener;
    }
}
