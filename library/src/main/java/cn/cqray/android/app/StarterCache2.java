package cn.cqray.android.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class StarterCache2 {

    /** id关键字 **/
    private static final String FRAGMENT_ID_KEY = "starter:fragment_id";
    private static final Map<FragmentManager, StarterCache> FC_DELEGATE_MAP = new HashMap<>();

    @NonNull
    static synchronized StarterCache get(LifecycleOwner owner) {
        FragmentManager fm = getFragmentManager(owner);
        StarterCache delegate = FC_DELEGATE_MAP.get(fm);
        if (delegate == null) {
            delegate = new StarterCache();
            FC_DELEGATE_MAP.put(fm, delegate);
        }
        return delegate;
    }

    static void remove(LifecycleOwner owner) {
        FragmentManager fm = getFragmentManager(owner);
        if (fm != null) {
            FC_DELEGATE_MAP.remove(getFragmentManager(owner));
        }
    }

    /** 容器Id **/
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int mContainerId;
    /** 回退栈 **/
    private final Stack<String> mBackStack = new Stack<>();
    /** Fragment管理器 **/
    private final FragmentManager mFragmentManager;

    private StarterCache2(FragmentManager fm) {
        mFragmentManager = fm;
    }

    @Nullable
    static FragmentManager getFragmentManager(LifecycleOwner owner) {
        try {
            FragmentManager fm;
            if (owner instanceof AppCompatActivity) {
                fm = ((AppCompatActivity) owner).getSupportFragmentManager();
            } else {
                Fragment fragment = (Fragment) owner;
                fm = fragment.getParentFragmentManager();
            }
            return fm;
        } catch (Exception ignore) {
            ignore.printStackTrace();
            return null;
        }
    }

    @NonNull
    String popFragmentTag() {
        return mBackStack.pop();
    }

    int getBackStackCount() {
        return mBackStack.size();
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
        if (mBackStack.isEmpty()) {
            return null;
        }
        return delegate.getFragmentManager().findFragmentByTag(getTopFragmentTag());
    }

    /**
     * 获取指定位置的Fragment标识
     * @param index 位置
     */
    @NonNull
    public String getFragmentTag(int index) {
        return mBackStack.get(index);
    }

    @Nullable
    public String getTopFragmentTag() {
        return mBackStack.isEmpty() ? null : mBackStack.peek();
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
