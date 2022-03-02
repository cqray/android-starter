package cn.cqray.android.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

/**
 * Fragment数据缓存
 * 用以管理AppCompatActivity和Fragment下的回退栈及其他缓存数据
 * @author cqray
 */
final class StarterCache {

    /** id关键字 **/
    private static final String FRAGMENT_ID_KEY = "starter:fragment_id";
    private static final Map<LifecycleOwner, StarterCache> FC_DELEGATE_MAP = new HashMap<>();

    @NonNull
    static synchronized StarterCache get(LifecycleOwner owner) {
        Object key;
        if (owner instanceof AppCompatActivity) {
            key = owner;
        } else {
            Fragment fragment = (Fragment) owner;
            Fragment parent = fragment.getParentFragment();
            if (parent != null) {
                key = parent;
            } else {
                key = fragment.requireActivity();
            }
        }
        StarterCache delegate = FC_DELEGATE_MAP.get(key);
        if (delegate == null) {
            delegate = new StarterCache();
            FC_DELEGATE_MAP.put(owner, delegate);
        }
        return delegate;
    }

    static void remove(LifecycleOwner owner) {
        FC_DELEGATE_MAP.remove(owner);
    }

    /** 容器Id **/
    private int mContainerId;
    /** 回退栈 **/
    private Stack<String> mBackStack = new Stack<>();

    @NonNull
    String popFragmentTag() {
        return mBackStack.pop();
    }

    int getBackStackCount() {
        return mBackStack.size();
    }

    int getContainerId() {
        return mContainerId;
    }

    void setContainerId(int containerId) {
        mContainerId = containerId;
    }

    void addToBackStack(String name) {
        mBackStack.add(name);
    }

    void popBackStackAfter(int index) {
        if (mBackStack.size() > index) {
            mBackStack.subList(index, mBackStack.size()).clear();
        }
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param delegate 代理
     * @param intent intent对象
     */
    @NonNull
    Fragment generateFragment(@NonNull StarterDelegate delegate, @NonNull NavIntent intent) {
        // Fragment工厂
        FragmentFactory factory = delegate.getFragmentManager().getFragmentFactory();
        // 类加载器
        ClassLoader loader = delegate.requireActivity().getClassLoader();
        // 获取参数
        Bundle arguments = intent.getArguments();
        // 设置ID
        arguments.putString(FRAGMENT_ID_KEY, UUID.randomUUID().toString().replace("-", ""));
        // 创建Fragment
        Fragment fragment = factory.instantiate(loader, intent.getToClass().getName());
        // 设置参数
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * 获取回退栈栈顶的Fragment
     * @param delegate 代理
     */
    @Nullable
    Fragment getTopFragment(@NonNull StarterDelegate delegate) {
        if (mBackStack.size() == 0) {
            return null;
        }
        String fragmentTag = getFragmentTag(mBackStack.size() - 1);
        return delegate.getFragmentManager().findFragmentByTag(fragmentTag);
    }

    /**
     * 获取指定位置的Fragment标识
     * @param index 位置
     */
    @NonNull
    public String getFragmentTag(int index) {
        return mBackStack.get(index);
    }

    /**
     * 获取Fragment对应的标识
     * @param fragment fragment对象
     */
    @NonNull
    public String getFragmentTag(@NonNull Fragment fragment) {
        Bundle arguments = fragment.getArguments();
        String id = arguments == null ? "" : arguments.getString(FRAGMENT_ID_KEY);
        return fragment.getClass().getName() + "-" + id;
    }

    /**
     * 是否是最先加载的Fragment
     * @param cls fragment对应class
     */
    public boolean isRootFragment(Class<?> cls) {
        if (mBackStack.isEmpty()) {
            return false;
        }
        String backStackName = mBackStack.firstElement();
        return backStackName.split("-")[0].equals(cls.getName());
    }
}
