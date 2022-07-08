package cn.cqray.android.launch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LaunchFragment2 extends DialogFragment {

    private Intent mIntent;

    public LaunchFragment2(Intent intent) {
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
                //Log.e("数据", GsonUtils.toJson(input.getComponent()));
                //return new Intent(requireContext(), ExceptionActivity.class).putExtras(input);
            }

            @Override
            public Intent parseResult(int resultCode, @Nullable Intent intent) {
                return intent;
            }
        }, new ActivityResultCallback<Intent>() {
            @Override
            public void onActivityResult(Intent result) {
                if (result == null) {
                    Log.e("数据", "毁掉了11|");
                } else {
                    Log.e("数据", "毁掉了22|");
                }
                dismiss();
            }
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
