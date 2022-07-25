package cn.cqray.android.tip;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;

/**
 * 提示适配器实现
 * @author Cqray
 * @date 2022/3/12
 */
class TipAdapterImpl implements TipAdapter {

    @Override
    public void show(@Nullable Object tag, TipLevel level, String text, int duration) {
        boolean longText = text != null && text.length() > 15;
        if (longText) {
            ToastUtils.showLong(text);
        } else {
            ToastUtils.showShort(text);
        }
    }
}
