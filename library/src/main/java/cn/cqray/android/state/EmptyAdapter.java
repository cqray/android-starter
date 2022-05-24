package cn.cqray.android.state;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;
import cn.cqray.android.util.ContextUtils;

/**
 * 空布局适配器
 * @author Cqray
 */
public class EmptyAdapter extends StateAdapter<EmptyAdapter> {

    /** 图片控件 **/
    protected ImageView mImageView;
    /** 文本控件 **/
    protected TextView mTextView;
    /** 重试控件 **/
    protected TextView mRetryView;
    /** 图片资源 **/
    protected Drawable mImageResource;

    public EmptyAdapter() {
        super(R.layout.starter_state_empty_layout);
        if (getClass().getSuperclass() == StateAdapter.class) {
            makeSureOverridden();
        }
        setDefaultText("暂无数据");
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        mImageView = view.findViewById(R.id.starter_image);
        mTextView = view.findViewById(R.id.starter_text);
        mRetryView = view.findViewById(R.id.starter_retry);
        mRetryView.setVisibility(View.GONE);
        if (mImageResource != null) {
            mImageView.setImageDrawable(mImageResource);
        }
    }

    @Override
    protected void onTextChanged(String text) {
        mTextView.setText(text);
    }

    @Override
    protected void onBackgroundChanged(Drawable background) {
        mContentView.setBackground(background);
    }

    @Override
    public void reset() {
        mImageView = null;
        mTextView = null;
        mRetryView = null;
        super.reset();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <S extends StateAdapter<S>> S deepClone() {
        EmptyAdapter adapter = super.deepClone();
        if (adapter != null) {
            adapter.mImageView = null;
            adapter.mTextView = null;
            adapter.mRetryView = null;
        }
        return (S) adapter;
    }

    public EmptyAdapter setImageResource(@DrawableRes int resId) {
        mImageResource = ContextCompat.getDrawable(ContextUtils.get(), resId);
        return this;
    }
}
