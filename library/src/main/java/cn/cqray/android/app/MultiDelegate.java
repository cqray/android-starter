package cn.cqray.android.app;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.cqray.android.ui.multi.MultiFragmentAdapter;

/**
 * 多Fragment管理委托
 * @author Cqray
 */
public class MultiDelegate {

    private int mContainerId;
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
        mFragments = Collections.synchronizedList(new ArrayList<>());
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
        loadMultiFragments(containerId, Arrays.asList(fragments));
    }

    /**
     * 加载多个Fragment
     * @param containerId 容器Id
     * @param fragments Fragment列表
     */
    public void loadMultiFragments(@IdRes int containerId, List<Fragment> fragments) {
        mContainerId = containerId;
        mCurrentIndex = 0;
        mFragments.clear();
        mFragments.addAll(fragments);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            Fragment temp;
            Fragment fragment = fragments.get(i);
            // 发现历史Fragment并移除
            String tag = fragments.get(i).getClass().getName() + "-" + i;
            temp = getFragmentManager().findFragmentByTag(tag);
            if (temp != null) {
                ft.remove(temp);
            }
            // 添加新的Fragment
            ft.add(containerId, fragment, tag);
            ft.setMaxLifecycle(fragment, i == mCurrentIndex ? Lifecycle.State.RESUMED : Lifecycle.State.CREATED);
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
        loadMultiFragments(vp, Arrays.asList(fragments));
    }

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param fragments Fragment列表
     */
    public void loadMultiFragments(@NonNull ViewPager2 vp, List<Fragment> fragments) {
        mViewPager = vp;
        mCurrentIndex = 0;
        mFragments.clear();
        mFragments.addAll(fragments);
        vp.setAdapter(getFragmentAdapter(mFragments));
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentIndex = position;
            }
        });
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
            mViewPager.setCurrentItem(index, mViewPager.isUserInputEnabled());
            mCurrentIndex = index;
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

    /**
     * 添加Fragment界面
     * @param intent 意图
     */
    public void addFragment(NavIntent intent) {
        addFragment(instantiateFragments(intent)[0]);
    }

    /**
     * 添加Fragment界面
     * @param cls Fragment类
     */
    public void addFragment(Class<? extends SupportProvider> cls) {
        addFragment(instantiateFragments(new NavIntent(cls))[0]);
    }

    /**
     * 添加Fragment界面
     * @param fragment Fragment
     */
    public void addFragment(Fragment fragment) {
        if (mViewPager != null) {
            MultiFragmentAdapter adapter = (MultiFragmentAdapter) mViewPager.getAdapter();
            assert adapter != null;
            adapter.getFragmentList().add(fragment);
            adapter.notifyItemInserted(adapter.getFragmentList().size() - 1);
        } else {
            mFragments.add(fragment);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(mContainerId, fragment);
            ft.setMaxLifecycle(fragment, Lifecycle.State.CREATED);
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 移除指定的Fragment界面
     * @param index 位置
     */
    public void removeFragment(int index) {
        Fragment fragment = mFragments.get(index);
        removeFragment(fragment);
    }

    /**
     * 移除Fragment界面
     * @param fragment Fragment
     */
    public void removeFragment(Fragment fragment) {
        int index = mFragments.indexOf(fragment);
        if (index < 0) {
            return;
        }
        if (mViewPager != null) {
            // 从ViewPager中移除Fragment
            MultiFragmentAdapter adapter = (MultiFragmentAdapter) mViewPager.getAdapter();
            assert adapter != null;
            adapter.getFragmentList().remove(fragment);
            adapter.notifyItemRemoved(index);
        } else {
            // 从列表中移除Fragment
            Fragment cur = null;
            mFragments.remove(fragment);
            if (index == 0 && mFragments.size() > 0) {
                cur = mFragments.get(0);
            } else if (mFragments.size() > 0) {
                cur = mFragments.get(index - 1);
            }
            // 从栈中移除Fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragment);
            if (cur != null) {
                ft.show(cur);
                ft.setMaxLifecycle(cur, Lifecycle.State.RESUMED);
            }
            ft.commitAllowingStateLoss();
        }
        // 获取当前Index
        if (mCurrentIndex == index) {
            if (mCurrentIndex != 0 && mFragments.size() > 0) {
                mCurrentIndex = mCurrentIndex - 1;
            }
        }
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

    /**
     * 生成Fragment列表
     * @param intents 意图列表
     */
    @NonNull
    private Fragment[] instantiateFragments(@NonNull NavIntent... intents) {
        Fragment[] fragments = new Fragment[intents.length];
        for (int i = 0; i < fragments.length; i++) {
            String className = intents[i].getToClass().getName();
            FragmentFactory fragmentFactory = getFragmentManager().getFragmentFactory();
            fragments[i] = fragmentFactory.instantiate(requireActivity().getClassLoader(), className);
            fragments[i].setArguments(intents[i].getArguments());
        }
        return fragments;
    }

}
