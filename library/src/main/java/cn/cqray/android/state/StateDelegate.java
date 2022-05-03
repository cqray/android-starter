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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.cqray.android.app.SupportActivity;
import cn.cqray.android.app.SupportFragment;
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
        StateDelegate delegate = START_DELEGATE_MAP.get(activity);
        if (delegate == null) {
            throw new IllegalStateException(NO_ATTACH_EXCEPTION);
        }
        return delegate;
    }

    @NonNull
    public static StateDelegate get(Fragment fragment) {
        StateDelegate delegate = START_DELEGATE_MAP.get(fragment);
        if (delegate == null) {
            throw new IllegalStateException(NO_ATTACH_EXCEPTION);
        }
        return delegate;
    }

    /** 父容器 **/
    private SmartRefreshLayout mParentView;
    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private ViewState mCurState = ViewState.IDLE;
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final SparseArray<StateAdapter> mAdapters = new SparseArray<>();

    private final Field[] mEnableFields = new Field[4];

    private boolean mHasToolbar;
    private FragmentActivity mActivity;

    /** 忙碌对话框 **/
    private BusyDialog mBusyDialog;


    @SneakyThrows
    public StateDelegate() {
        Class<?> cls = SmartRefreshLayout.class;
        mEnableFields[0] = cls.getDeclaredField("mEnableRefresh");
        mEnableFields[1] = cls.getDeclaredField("mEnableLoadMore");
        mEnableFields[2] = cls.getDeclaredField("mEnableOverScrollDrag");
        mEnableFields[3] = cls.getDeclaredField("mManualLoadMore");
    }

    public void attachRefreshLayout(SmartRefreshLayout layout) {
        if (START_DELEGATE_MAP.containsValue(this)) {
            throw new RuntimeException(StateDelegate.class.getName() + " has attached.");
        }
        mParentView = layout;
        mParentView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

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

    public void attchActivity(FragmentActivity activity) {
        mActivity = activity;
        if (mActivity instanceof SupportActivity) {
            mHasToolbar = ((SupportActivity) mActivity).mToolbar != null;
        }
        mActivity.getLifecycle().addObserver((LifecycleEventObserver) (owner, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                START_DELEGATE_MAP.remove(activity);
            }
        });
        START_DELEGATE_MAP.put(activity, this);
    }

    public void attachFramgent(@NonNull Fragment fragment) {
        mActivity = fragment.requireActivity();
        if (fragment instanceof SupportFragment) {
            mHasToolbar = ((SupportFragment) fragment).mToolbar != null;
        }
        fragment.getLifecycle().addObserver((LifecycleEventObserver) (owner, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                START_DELEGATE_MAP.remove(fragment);
            }
        });
        START_DELEGATE_MAP.put(fragment, this);
    }

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
        saveEnableState();
        mCurState = state;
        // 初始化界面
        initStateLayouts();
        if (mRootLayout != null) {
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
            restoreEnableState();
        } else  {
            if (state == ViewState.BUSY && mBusyDialog == null) {
                mBusyDialog = new BusyDialog();
                mBusyDialog.show(mActivity.getSupportFragmentManager(), null);
            } else if (mBusyDialog != null) {
                mBusyDialog.dismiss();
                mBusyDialog = null;
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

    private void initStateLayouts() {
        if (mRootLayout != null || mParentView == null) {
            return;
        }
        if (!START_DELEGATE_MAP.containsValue(this)) {
            throw new RuntimeException(NO_ATTACH_EXCEPTION);
        }

        // attach SmartRefreshLayout
        if (mParentView != null) {

            Context context = mParentView.getContext();
            mRootLayout = new FrameLayout(context);
            mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            mRootLayout.setClickable(true);
            mRootLayout.setFocusable(true);
            // 替换布局
            List<View> children = new ArrayList<>();
            FrameLayout refreshContent = new FrameLayout(context);
            refreshContent.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            for (int i = 0; i < mParentView.getChildCount(); i++) {
                View view = mParentView.getChildAt(i);
                if (view instanceof RefreshHeader || view instanceof RefreshFooter) {
                    continue;
                }
                children.add(view);
            }
            for (int i = 0; i < children.size(); i++) {
                View view = children.get(i);
                mParentView.removeView(view);
                refreshContent.addView(view, i);
            }
            refreshContent.addView(mRootLayout);
            mParentView.setRefreshContent(refreshContent);
        }

//        // attach RootView
//        if (mChildView instanceof FrameLayout) {
//            FrameLayout parent = (FrameLayout) mChildView;
//            List<View> children = new ArrayList<>();
//            for (int i = 0; i < parent.getChildCount(); i++) {
//                View view = parent.getChildAt(i);
//                children.add(view);
//            }
//            for (int i = 0; i < children.size(); i++) {
//                View view = children.get(i);
//                parent.removeView(view);
//                mRootLayout.addView(view, i);
//            }
//            parent.addView(mRootLayout);
//        }
    }

    /**
     * 保存刷新控件状态
     */
    @SneakyThrows
    private void saveEnableState() {
        if (mParentView != null && mCurState == ViewState.IDLE) {
            mEnableStates[0] = mEnableFields[0].getBoolean(mParentView);
            mEnableStates[1] = mEnableFields[1].getBoolean(mParentView);
            mEnableStates[2] = mEnableFields[2].getBoolean(mParentView);
        }
    }

    /**
     * 恢复启用状态
     */
    @SneakyThrows
    private void restoreEnableState() {
        if (mParentView != null) {
            if (mCurState == ViewState.IDLE) {
                mParentView.setEnableRefresh(mEnableStates[0]);
                mParentView.setEnableLoadMore(mEnableStates[1]);
                mParentView.setEnableOverScrollDrag(mEnableStates[2]);
            } else {
                mParentView.setEnableRefresh(mCurState != ViewState.BUSY && mEnableStates[0]);
                mParentView.setEnableLoadMore(false);
                mParentView.setEnableOverScrollDrag(false);
                mEnableFields[3].set(mParentView, true);
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
        if (adapter != null) {
            mAdapters.put(state.ordinal(), adapter);
            adapter.onAttach(this, mRootLayout);
        }
        return adapter;
    }
}
