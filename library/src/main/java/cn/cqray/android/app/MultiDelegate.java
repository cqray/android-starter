package cn.cqray.android.app;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.cqray.android.ui.multi.MultiFragmentAdapter;

/**
 * 多Fragment管理委托
 * @author Cqray
 */
public class MultiDelegate {

    private int mCurrentIndex = 0;
    private ViewPager2 mViewPager;
    private final List<Fragment> mFragments;
    private final LifecycleOwner mLifecycleOwner;

    public MultiDelegate(AppCompatActivity activity) {
        this((LifecycleOwner) activity);
    }

    public MultiDelegate(Fragment fragment) {
        this((LifecycleOwner) fragment);
    }

    private MultiDelegate(LifecycleOwner owner) {
        mLifecycleOwner = owner;
        mFragments = new ArrayList<>();
    }

    /**
     * 加载多个Fragment
     * @param containerId 容器Id
     * @param intents 意图列表
     */
    public void loadMultiFragments(@IdRes int containerId, NavIntent... intents) {
        Fragment[] fragments = instantiateFragments(intents);
        loadMultiFragments(containerId, fragments);
    }

    /**
     * 加载多个Fragment
     * @param containerId 容器Id
     * @param fragments Fragment列表
     */
    public void loadMultiFragments(@IdRes int containerId, Fragment...fragments) {
        mFragments.addAll(Arrays.asList(fragments));
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            ft.add(containerId, fragments[i]);
            ft.setMaxLifecycle(fragments[i], i == mCurrentIndex ? Lifecycle.State.RESUMED : Lifecycle.State.CREATED);
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param intents 意图列表
     */
    public void loadMultiFragments(@NonNull ViewPager2 vp, NavIntent... intents) {
        Fragment[] fragments = instantiateFragments(intents);
        loadMultiFragments(vp, fragments);
    }

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param fragments Fragment列表
     */
    public void loadMultiFragments(@NonNull ViewPager2 vp, Fragment... fragments) {
        mViewPager = vp;
        mFragments.addAll(Arrays.asList(fragments));
        vp.setAdapter(getFragmentAdapter(mFragments));
        View child = vp.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    /**
     * 显示指定位置的Fragment
     * @param index 指定位置
     */
    public void showFragment(int index) {
        if (mFragments.isEmpty() || index >= mFragments.size()) {
            return;
        }
        if (mViewPager != null) {
            ((ViewPager2) mViewPager).setCurrentItem(index, mViewPager.isUserInputEnabled());
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = mFragments.get(index);
        if (index == mCurrentIndex) {
            Lifecycle.State state = fragment.getLifecycle().getCurrentState();
            if (!state.isAtLeast(Lifecycle.State.RESUMED)) {
                ft.show(fragment);
                ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
                ft.commitAllowingStateLoss();
            }
        } else {
            Fragment cur = mFragments.get(mCurrentIndex);
            ft.hide(cur);
            ft.setMaxLifecycle(cur, Lifecycle.State.STARTED);
            ft.show(fragment);
            ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
            ft.commitAllowingStateLoss();
        }
        mCurrentIndex = index;
    }

    /**
     * 显示指定的Fragment
     * @param fragment 指定Fragment
     */
    public void showFragment(Fragment fragment) {
        int index = mFragments.indexOf(fragment);
        showFragment(index);
    }

    public void reset() {
        for (Fragment fragment : mFragments) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
        mCurrentIndex = 0;
        mFragments.clear();
        mViewPager = null;
    }

    @NonNull
    public FragmentManager getFragmentManager() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return ((AppCompatActivity) mLifecycleOwner).getSupportFragmentManager();
        }
        return ((Fragment) mLifecycleOwner).getChildFragmentManager();
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    @Nullable
    public AppCompatActivity getActivity() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return (AppCompatActivity) mLifecycleOwner;
        }
        return (AppCompatActivity) ((Fragment) mLifecycleOwner).getActivity();
    }

    @NonNull
    public AppCompatActivity requireActivity() {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return (AppCompatActivity) mLifecycleOwner;
        }
        return (AppCompatActivity) ((Fragment) mLifecycleOwner).requireActivity();
    }

    @NonNull
    private MultiFragmentAdapter getFragmentAdapter(List<Fragment> fragmentList) {
        if (mLifecycleOwner instanceof AppCompatActivity) {
            return new MultiFragmentAdapter((AppCompatActivity) mLifecycleOwner, fragmentList);
        } else {
            return new MultiFragmentAdapter((Fragment) mLifecycleOwner, fragmentList);
        }
    }

    @NonNull
    private Fragment[] instantiateFragments(@NonNull NavIntent... intents) {
        Fragment[] fragments = new Fragment[intents.length];
        for (int i = 0; i < fragments.length; i++) {
            NavIntent intent = intents[i];
            fragments[i] = getFragmentManager().getFragmentFactory()
                    .instantiate(requireActivity().getClassLoader(), intent.getToClass().getName());
            fragments[i].setArguments(intent.getArguments());
        }
        return fragments;
    }

}
