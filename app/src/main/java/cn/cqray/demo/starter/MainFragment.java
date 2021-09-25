package cn.cqray.demo.starter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;

import cn.cqray.android.app.NavIntent;
import cn.cqray.android.app.SupportFragment;
import cn.cqray.android.widget.ActionLayout;
import cn.cqray.android.widget.Toolbar;

/**
 * @author Admin
 * @date 2021/9/23 17:31
 */
public class MainFragment extends SupportFragment {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = view.findViewById(R.id.tv);

        tv.setBackgroundColor(Color.RED);
        Drawable drawable = createMaterialShapeDrawableBackground(tv);
        ViewCompat.setBackground(tv, drawable);

        MaterialShapeUtils.setElevation(tv, 30);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(new NavIntent(MainFragment2.class));
            }
        });
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setElevation(30);


        ActionLayout al = view.findViewById(R.id.action);
        al.setText(0, "66666").setActionVisible(0, true)
                .setText(1, "77777777")
                .setActionVisible(1,true);
    }


    @NonNull
    private MaterialShapeDrawable createMaterialShapeDrawableBackground(View view) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        Drawable originalBackground = view.getBackground();
        if (originalBackground instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(
                    ColorStateList.valueOf(((ColorDrawable) originalBackground).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(view.getContext());
        return materialShapeDrawable;
    }
}
