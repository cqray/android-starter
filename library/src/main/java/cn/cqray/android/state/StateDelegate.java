package cn.cqray.android.state;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.cqray.android.view.ViewDelegate;
import cn.cqray.android.view.ViewProvider;
import lombok.SneakyThrows;

/**
 * 状态管理委托
 * @author Cqray
 */
public class StateDelegate {

    /** 委托缓存Map **/
    private static final Map<Object, StateDelegate> START_DELEGATE_MAP = new ConcurrentHashMap<>();
    /** SmartLayout一些Enable属性 **/
    private static final Field[] SMART_ENABLE_FIELDS = new Field[4];

    private static final String NO_ATTACH_EXCEPTION = "Do you forget call attachParent() or attachChild().";

    static {
        // 静态反射初始化一些属性
        Class<?> cls = SmartRefreshLayout.class;
        try {
            SMART_ENABLE_FIELDS[0] = cls.getDeclaredField("mEnableRefresh");
            SMART_ENABLE_FIELDS[1] = cls.getDeclaredField("mEnableLoadMore");
            SMART_ENABLE_FIELDS[2] = cls.getDeclaredField("mEnableOverScrollDrag");
            SMART_ENABLE_FIELDS[3] = cls.getDeclaredField("mManualLoadMore");
        } catch (NoSuchFieldException ignore) {}
    }

    @NonNull
    public static StateDelegate get(View view) {
        StateDelegate delegate = START_DELEGATE_MAP.get(view);
        if (delegate == null) {
            throw new IllegalStateException(NO_ATTACH_EXCEPTION);
        }
        return delegate;
    }

    @NonNull
    public static StateDelegate get(FragmentActivity activity) {
        return get((LifecycleOwner) activity);
    }

    @NonNull
    public static StateDelegate get(Fragment fragment) {
        return get((LifecycleOwner) fragment);
    }

    @NonNull
    private synchronized static StateDelegate get(@NonNull LifecycleOwner owner) {
        StateDelegate delegate = START_DELEGATE_MAP.get(owner);
        if (delegate == null) {
            delegate = new StateDelegate(owner);
        }
        return delegate;
    }

    /** 父容器 **/
    private SmartRefreshLayout mRefreshLayout;
    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private ViewState mCurState = ViewState.IDLE;
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final SparseArray<StateAdapter> mAdapters = new SparseArray<>();
//    private View mContentView;
//    private FragmentActivity mActivity;

    /** 忙碌对话框 **/
    private StateDialog mStateDialog;
    private LifecycleOwner mLifecycleOwner;

    private StateDelegate(@NonNull LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (owner, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                START_DELEGATE_MAP.remove(lifecycleOwner);
            }
        });
        START_DELEGATE_MAP.put(lifecycleOwner, this);
    }

    public void attachLayout(FrameLayout layout) {

    }

    public void attachLayout(SmartRefreshLayout layout) {

    }

    public void attachRefreshLayout(SmartRefreshLayout layout) {
        if (START_DELEGATE_MAP.containsValue(this)) {
            throw new RuntimeException(StateDelegate.class.getName() + " has attached.");
        }
        mRefreshLayout = layout;
        mRefreshLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                START_DELEGATE_MAP.put(v, StateDelegate.this);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                START_DELEGATE_MAP.remove(v);
            }
        });
        START_DELEGATE_MAP.put(layout, this);
    }

//    public void attchActivity(FragmentActivity activity) {
//        mActivity = activity;
//        if (mActivity instanceof SupportActivity) {
//        }
//        mActivity.getLifecycle().addObserver((LifecycleEventObserver) (owner, event) -> {
//            if (event == Lifecycle.Event.ON_DESTROY) {
//                START_DELEGATE_MAP.remove(activity);
//            }
//        });
//        START_DELEGATE_MAP.put(activity, this);
//    }
//
//    public void attachFragment(@NonNull Fragment fragment) {
//        mActivity = fragment.requireActivity();
//        if (fragment instanceof SupportFragment) {
//
//            mContentView = ((SupportFragment) fragment).getViewDelegate().getContentView();
//        }
//        fragment.getLifecycle().addObserver((LifecycleEventObserver) (owner, event) -> {
//            if (event == Lifecycle.Event.ON_DESTROY) {
//                START_DELEGATE_MAP.remove(fragment);
//            }
//        });
//        START_DELEGATE_MAP.put(fragment, this);
//    }

    public void setIdle() {
        setState(ViewState.IDLE, null);
    }

    public void setBusy() {
        setState(ViewState.BUSY, null);
    }

    public void setBusy(String text) {
        setState(ViewState.BUSY, text);
    }

    public void setEmpty() {
        setState(ViewState.EMPTY, null);
    }

    public void setEmpty(String text) {
        setState(ViewState.EMPTY, text);
    }

    public void setError() {
        setState(ViewState.ERROR, null);
    }

    public void setError(String text) {
        setState(ViewState.ERROR, text);
    }

    public void setState(ViewState state, String text) {
        saveRefreshEnableState();
        if (mRootLayout != null) {
            mCurState = state;
            // 初始化界面
            initStateLayouts();
            // 初始化状态
            if (mCurState != ViewState.BUSY) {
                for (int i = 0; i < mAdapters.size(); i++) {
                    StateAdapter adapter = mAdapters.valueAt(i);
                    if (adapter != null) {
                        adapter.hide();
                    }
                }
            }
            // 显示指定状态的界面
            StateAdapter adapter = getAdapter(mCurState);
            if (adapter != null) {
                adapter.show(text);
            }
            restoreRefreshEnableState();
        } else  {
            if (state == ViewState.IDLE || state != mCurState) {
                if (mStateDialog != null) {
                    mStateDialog.dismiss();
                    mStateDialog = null;
                }
            }
            mCurState = state;

            // 如果不是空闲状态，则显示对应对话框
            if (state != ViewState.IDLE) {
                mStateDialog = new StateDialog(mCurState);
                mStateDialog.setLocationAt(getLocationView());
                mStateDialog.show(getSupportFragmentManager(), null);
            }
        }
    }

    public void setBusyAdapter(StateAdapter adapter) {
        setStateAdapter(ViewState.BUSY, adapter);
    }

    public void setEmptyAdapter(StateAdapter adapter) {
        setStateAdapter(ViewState.EMPTY, adapter);
    }

    public void setErrorAdapter(StateAdapter adapter) {
        setStateAdapter(ViewState.ERROR, adapter);
    }

    public boolean isBusy() {
        return mCurState == ViewState.BUSY;
    }

    private void initStateLayouts() {
        if (mRootLayout != null || mRefreshLayout == null) {
            return;
        }
        if (!START_DELEGATE_MAP.containsValue(this)) {
            throw new RuntimeException(NO_ATTACH_EXCEPTION);
        }
        Context context = mRefreshLayout.getContext();
        mRootLayout = new FrameLayout(context);
        mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRootLayout.setClickable(true);
        mRootLayout.setFocusable(true);
        // 替换布局
        List<View> children = new ArrayList<>();
        FrameLayout refreshContent = new FrameLayout(context);
        refreshContent.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        for (int i = 0; i < mRefreshLayout.getChildCount(); i++) {
            View view = mRefreshLayout.getChildAt(i);
            if (view instanceof RefreshHeader || view instanceof RefreshFooter) {
                continue;
            }
            children.add(view);
        }
        for (int i = 0; i < children.size(); i++) {
            View view = children.get(i);
            mRefreshLayout.removeView(view);
            refreshContent.addView(view, i);
        }
        refreshContent.addView(mRootLayout);
        mRefreshLayout.setRefreshContent(refreshContent);
    }

    /**
     * 保存刷新控件启用状态
     */
    @SneakyThrows
    private void saveRefreshEnableState() {
        if (mRefreshLayout != null && mCurState == ViewState.IDLE) {
            mEnableStates[0] = SMART_ENABLE_FIELDS[0].getBoolean(mRefreshLayout);
            mEnableStates[1] = SMART_ENABLE_FIELDS[1].getBoolean(mRefreshLayout);
            mEnableStates[2] = SMART_ENABLE_FIELDS[2].getBoolean(mRefreshLayout);
        }
    }

    /**
     * 恢复刷新控件启用状态
     */
    @SneakyThrows
    private void restoreRefreshEnableState() {
        if (mRefreshLayout != null) {
            if (mCurState == ViewState.IDLE) {
                mRefreshLayout.setEnableRefresh(mEnableStates[0]);
                mRefreshLayout.setEnableLoadMore(mEnableStates[1]);
                mRefreshLayout.setEnableOverScrollDrag(mEnableStates[2]);
            } else {
                mRefreshLayout.setEnableRefresh(mCurState != ViewState.BUSY && mEnableStates[0]);
                mRefreshLayout.setEnableLoadMore(false);
                mRefreshLayout.setEnableOverScrollDrag(false);
                SMART_ENABLE_FIELDS[3].set(mRefreshLayout, true);
            }
        }
    }

    /**
     * 设置对应的状态适配器
     * @param state 状态
     * @param adapter 适配器
     */
    private void setStateAdapter(@NonNull ViewState state, StateAdapter adapter) {
        StateAdapter sAdapter = mAdapters.get(state.ordinal());
        if (sAdapter != null) {
            sAdapter.hide();
        }
        mAdapters.put(state.ordinal(), adapter);
    }

    /**
     * 获取对应状态的适配器
     * @param state 指定状态
     */
    @Nullable
    private StateAdapter getAdapter(@NonNull ViewState state) {
        StateAdapter adapter = mAdapters.get(state.ordinal());
        if (adapter == null) {
            if (state == ViewState.BUSY) {
                adapter = new BusyAdapter();
            } else if (state == ViewState.EMPTY) {
                adapter = new EmptyAdapter();
            } else if (state == ViewState.ERROR) {
                adapter = new ErrorAdapter();
            }
        }
        if (adapter != null && !adapter.isAttached()) {
            mAdapters.put(state.ordinal(), adapter);
            adapter.onAttach(this, mRootLayout);
        }
        return adapter;
    }

    @NonNull
    private FragmentManager getSupportFragmentManager() {
        if (mLifecycleOwner instanceof FragmentActivity) {
            return ((FragmentActivity) mLifecycleOwner).getSupportFragmentManager();
        } else {
            Fragment fragment = (Fragment) mLifecycleOwner;
            return fragment.requireActivity().getSupportFragmentManager();
        }
    }

    @Nullable
    private View getLocationView() {
        if (mLifecycleOwner instanceof ViewProvider) {
            ViewDelegate delegate = ((ViewProvider) mLifecycleOwner).getViewDelegate();
            return delegate.getContentView();
        }
        return null;
    }
}
