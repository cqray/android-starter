package cn.cqray.android.tip;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import cn.cqray.android.Starter;

/**
 * 提示适配器实现
 * @author Cqray
 * @date 2022/3/12
 */
class TipAdapterImpl implements TipAdapter {

    @Override
    public void show(@Nullable Object tag, TipLevel level, String text, int duration) {
        Context context = Starter.getInstance().getContext();
        Toast.makeText(context, text, duration).show();
    }
}
