package cn.cqray.android.state;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;

import cn.cqray.android.R;

/**
 * 忙碌界面适配器
 * @author Cqray
 */
public class BusyAdapter extends StateAdapter<BusyAdapter> {

    /** 控件颜色 **/
    protected Integer mColor;
    /** 忙碌样式 **/
    protected Style mSpinStyle;
    /** 忙碌样式控件 **/
    protected SpinKitView mSpinKitView;
    /** 文本控件 **/
    protected TextView mTextView;

    public BusyAdapter() {
        super(R.layout.starter_state_busy_layout);
        if (getClass().getSuperclass() == StateAdapter.class) {
            makeSureOverridden();
        }
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        mSpinKitView = view.findViewById(R.id.starter_spin_kit);
        mTextView = view.findViewById(R.id.starter_text);
        if (mColor != null) {
            mSpinKitView.setColor(mColor);
        }
        if (mSpinStyle != null) {
            Sprite sprite = SpriteFactory.create(mSpinStyle);
            mSpinKitView.setIndeterminateDrawable(sprite);
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
        mSpinKitView = null;
        mTextView = null;
        super.reset();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <S extends StateAdapter<S>> S deepClone() {
        BusyAdapter adapter = super.deepClone();
        if (adapter != null) {
            adapter.mSpinKitView = null;
            adapter.mTextView = null;
        }
        return (S) adapter;
    }

    public BusyAdapter setSpinColor(int color) {
        mColor = color;
        return this;
    }

    public BusyAdapter setSpinStyle(Style spinStyle) {
        mSpinStyle = spinStyle;
        return this;
    }
}
