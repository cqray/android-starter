package cn.cqray.android.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.cqray.android.anim.FragmentAnimator;

/**
 * 基础Fragment
 * @author Cqray
 */
public class SupportFragment extends Fragment implements StarterProvider {

    private ViewDelegate mViewDelegate = new ViewDelegate(this);
    private StarterDelegate mStarterDelegate = StarterDelegate.get(this);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreating(savedInstanceState);
        View view = mViewDelegate.getContentView() == null
                ? super.onCreateView(inflater, container, savedInstanceState)
                : mViewDelegate.getContentView();
        return view;
        //return mDelegate.getSwipeDelegate().onAttachFragment(view);
    }

    public void onCreating(@Nullable Bundle savedInstanceState) {}

    public void setContentView(View view) {
        mViewDelegate.setContentView(view);
    }

    public void setContentView(int layoutResId) {
        mViewDelegate.setContentView(layoutResId);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mViewDelegate.setContentView(view);
    }

    public void setNativeContentView(View view) {
        mViewDelegate.setNativeContentView(view);
    }

    public void setNativeContentView(int layoutResId) {
        mViewDelegate.setNativeContentView(layoutResId);
    }

    public void setHeaderView(int layoutResId) {
        mViewDelegate.setHeaderView(layoutResId);
    }

    public void setHeaderView(View view) {
        mViewDelegate.setHeaderView(view);
    }

    public void setHeaderFloating(boolean floating) {
        mViewDelegate.setHeaderFloating(floating);
    }

    public void setFooterView(int layoutResId) {
        mViewDelegate.setFooterView(layoutResId);
    }

    public void setFooterView(View view) {
        mViewDelegate.setFooterView(view);
    }

    public void setFooterFloating(boolean floating) {
        mViewDelegate.setFooterFloating(floating);
    }

    public void setBackgroundRes(@DrawableRes int resId) {
        mViewDelegate.setBackgroundRes(resId);
    }

    public void setBackgroundColor(int color) {
        mViewDelegate.setBackgroundColor(color);
    }

    public void setBackground(Drawable background) {
        mViewDelegate.setBackground(background);
    }

    @Override
    public StarterDelegate getStarterDelegate() {
        return mStarterDelegate;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return null;
    }

    @Override
    public boolean onBackPressedSupport() {
        return false;
    }

    public void start(NavIntent intent) {
        mStarterDelegate.start(intent);
    }
}
