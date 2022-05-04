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

public class BusyDialog extends DialogFragment {

    private View mContentView;

    public BusyDialog(View rootView) {
        mContentView = rootView;
        setCancelable(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScreenUtils.setFullScreen(requireActivity());

//        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        WindowManager.LayoutParams lp = requireActivity().getWindow().getAttributes();
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        requireActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) view;
        StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
        StateAdapter adapter = ExtUtils.deepClone(strategy.getBusyAdapter());
        adapter.onAttach(null, root);
        root.addView(adapter.getContentView());

//        mContentView.post(() -> {
//
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) adapter.getContentView().getLayoutParams();
//            params.topMargin = 200;// mContentView.findViewById(R.id.starter_toolbar).getBottom();
//            adapter.getContentView().requestLayout();
//            adapter.getContentView().setBackgroundColor(Color.CYAN);
//        });
        //adapter.getContentView().requestLayout();
        //adapter.getContentView().setBackgroundColor(Color.BLACK);

        mContentView.post(() -> {

            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.width = -1;//ScreenUtils.getAppScreenWidth();
            lp.height = ScreenUtils.getAppScreenHeight() - mContentView.findViewById(R.id.starter_toolbar).getBottom();
            lp.gravity = Gravity.TOP;
            lp.y = mContentView.findViewById(R.id.starter_toolbar).getBottom();
            getDialog().getWindow().setAttributes(lp);

            view.setVisibility(View.VISIBLE);
        });

//        View view2 = getDialog().getWindow().getDecorView();
//        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-1, ScreenUtils.getAppScreenHeight() -200);
//        marginLayoutParams.topMargin = 200;
//        view2.setLayoutParams(marginLayoutParams);

        //view2.setBackgroundColor(Color.BLACK);
        //view2.setPadding(0, 200, 0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout layout = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);

        //params.topMargin = 200;
        layout.setClickable(true);
        layout.setFocusable(true);
        layout.setLayoutParams(params);
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
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0f);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置为可以点击区域外的控件
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // 设置满屏

        return dlg;
    }
}
