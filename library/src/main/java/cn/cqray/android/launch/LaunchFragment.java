package cn.cqray.android.launch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * 用于跳转的Fragment
 * @author Cqray
 */
public class LaunchFragment extends DialogFragment {

    private Intent mIntent;
    private ResultCallback mCallback;

    public LaunchFragment(Intent intent) {
        mIntent = intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForActivityResult(new ActivityResultContract<Intent, Intent>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Intent input) {
                return input;
            }

            @Override
            public Intent parseResult(int resultCode, @Nullable Intent intent) {
                return intent;
            }
        }, result -> {
            if (mCallback != null) {
                if (result == null) {
                    // 没有回调成功
                    mCallback.onFail();
                } else {
                    // 回调成功
                    mCallback.onSucceed(result);
                }
            }
            dismiss();
        }).launch(mIntent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dlg = new Dialog(requireActivity());
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setCancelable(false);
        Window window = dlg.getWindow();
        assert window != null;
        // 设置为可以点击区域外的控件
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // 遮罩透明度
        window.setDimAmount(0f);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dlg;
    }
}
