package cn.cqray.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 回退控件
 * @author Cqray
 */
public class BackView extends LinearLayout {

    private AppCompatImageView mBackIcon;
    private AppCompatTextView mBackText;

    public BackView(Context context) {
        this(context, null);
    }

    public BackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mBackIcon = new AppCompatImageView(context);
        mBackText = new AppCompatTextView(context);


        mBackText.setText("455555");
        addView(mBackIcon);
        addView(mBackText);
    }
}
