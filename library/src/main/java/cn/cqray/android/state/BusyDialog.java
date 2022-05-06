package cn.cqray.android.state;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.ScreenUtils;

import cn.cqray.android.R;
import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.util.ExtUtils;

/**
 * 状态对话框
 * @author LeiJue
 * @date 2022/5/5
 */
public class BusyDialog extends DialogFragment {

    private String mText;
    private View mLocationView;
    private StateAdapter mBusyAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) view;
        // 添加忙碌界面
        StateAdapter adapter = getBusyAdapter();
        adapter.onAttach(null, root);
        adapter.show(mText);
        root.addView(adapter.getContentView());
        // 延时任务
        Runnable runnable = () -> {
            // 计算对话框显示的高度
            int height = mLocationView == null ? ScreenUtils.getAppScreenHeight() : mLocationView.getHeight();
            // 偏移量
            int offset = 0;
            if (mLocationView != null) {
                // 如果标记了内容控件，则有标题栏存在
                View toolbar = mLocationView.findViewById(R.id.starter_toolbar);
                if (toolbar != null) {
                    // 获取标题栏底部位置为偏移量
                    offset = toolbar.getBottom();
                }
            }
            // 设置对话框的大小和位置
            Window window = requireDialog().getWindow();
            assert window != null;
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = -1;
            lp.height = height - offset;
            lp.gravity = Gravity.TOP;
            lp.y = offset;
            window.setAttributes(lp);
            // 显示布局界面
            requireView().setVisibility(View.VISIBLE);
        };

        // 运行任务
        if (mLocationView == null) {
            runnable.run();
        } else {
            mLocationView.post(runnable);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout layout = new FrameLayout(requireContext());
        layout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        layout.setVisibility(View.INVISIBLE);
        return layout;
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
        // 设置显示遮罩
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // 遮罩透明度
        window.setDimAmount(0f);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dlg;
    }

    private StateAdapter getBusyAdapter() {
        if (mBusyAdapter != null) {
            return mBusyAdapter;
        }
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        StateAdapter adapter = strategy.getBusyAdapter();
        if (adapter != null) {
            return ExtUtils.deepClone(adapter);
        }
        return new BusyAdapter();
    }

    public void setLocationAt(View view) {
        mLocationView = view;
    }

    public void setBusyAdapter(StateAdapter adapter) {
        mBusyAdapter = adapter;
    }

    public void setText(String text) {
        mText = text;
    }
}
