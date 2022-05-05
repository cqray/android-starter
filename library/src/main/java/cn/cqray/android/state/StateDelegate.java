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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.exception.ExceptionDispatcher;
import cn.cqray.android.view.ViewDelegate;
import cn.cqray.android.view.ViewProvider;

/**
 * 状态管理委托
 * @author Cqray
 */
public class StateDelegate {

    /** 委托缓存Map **/
    private static final Map<LifecycleOwner, StateDelegate> START_DELEGATE_MAP = new ConcurrentHashMap<>();
    /** SmartLayout一些Enable属性 **/
    private static final Field[] SMART_ENABLE_FIELDS = new Field[4];

    static {
        // 静态反射初始化一些属性
        Class<?> cls = SmartRefreshLayout.class;
        try {
            SMART_ENABLE_FIELDS[0] = cls.getDeclaredField("mEnableRefresh");
            SMART_ENABLE_FIELDS[1] = cls.getDeclaredField("mEnableLoadMore");
            SMART_ENABLE_FIELDS[2] = cls.getDeclaredField("mEnableOverScrollDrag");
            SMART_ENABLE_FIELDS[3] = cls.getDeclaredField("mManualLoadMore");
            SMART_ENABLE_FIELDS[0].setAccessible(true);
            SMART_ENABLE_FIELDS[1].setAccessible(true);
            SMART_ENABLE_FIELDS[2].setAccessible(true);
            SMART_ENABLE_FIELDS[3].setAccessible(true);
        } catch (NoSuchFieldException ignore) {}
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

    /** 忙碌状态是否可取消 **/
    private boolean mBusyCancelable;
    /** 父容器 **/
    private SmartRefreshLayout mRefreshLayout;
    /** 常规界面 **/
    private FrameLayout mNormalLayout;
    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private ViewState mCurState = ViewState.IDLE;
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final SparseArray<StateAdapter> mAdapters = new SparseArray<>();

    /** 忙碌对话框 **/
    private BusyDialog mBusyDialog;
    private LifecycleOwner mLifecycleOwner;

    private StateDelegate(@NonNull LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (owner, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                START_DELEGATE_MAP.remove(lifecycleOwner);
            }
        });
        START_DELEGATE_MAP.put(lifecycleOwner, this);
        mBusyCancelable = Starter.getInstance().getStarterStrategy().isBusyCancelable();
    }

    public void attachLayout(FrameLayout layout) {
        mNormalLayout = layout;
    }

    public void attachLayout(SmartRefreshLayout layout) {
        mRefreshLayout = layout;
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
        // 如果接入了指定容器
        if (mNormalLayout != null || mRefreshLayout != null) {
            // 保存刷新控件状态
            saveRefreshEnableState();
            // 初始化控件
            initStateLayouts();
            // 初始化状态
            if (state != ViewState.BUSY) {
                for (int i = 0; i < mAdapters.size(); i++) {
                    StateAdapter adapter = mAdapters.valueAt(i);
                    if (adapter != null) {
                        adapter.hide();
                    }
                }
            }
            // 显示指定状态的界面
            StateAdapter adapter = getAdapter(state);
            if (adapter != null) {
                adapter.show(text);
            }
            // 接入根容器
            if (adapter != null && !adapter.isAttached()) {
                adapter.onAttach(this, mRootLayout);
            }
            // 恢复刷新控件状态
            restoreRefreshEnableState();
        } else if (state != mCurState) {
            // 如果是空闲状态或者和当前状态不一致
            if (state == ViewState.IDLE) {
                // 取消忙碌对话框
                if (mBusyDialog != null) {
                    mBusyDialog.dismiss();
                    mBusyDialog = null;
                }
            }
            // 如果是忙碌状态，则显示对应对话框
            if (state == ViewState.BUSY) {
                mBusyDialog = new BusyDialog();
                mBusyDialog.setCancelable(mBusyCancelable);
                mBusyDialog.setBusyAdapter(getAdapter(ViewState.BUSY));
                mBusyDialog.setLocationAt(getLocationView());
                mBusyDialog.show(getSupportFragmentManager(), null);
            } else if (state != ViewState.IDLE) {
                ExceptionDispatcher.dispatchStarterThrowable(this,
                        String.format(Locale.getDefault(), "不支持%s状态", state.name()),
                        String.format(Locale.getDefault(), "需要调用attachLayout()后，才能支持%s和%s状态",
                                ViewState.EMPTY.name(), ViewState.ERROR.name()));
            }
        }
        mCurState = state;
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

    public void setBusyCancelable(boolean cancelable) {
        mBusyCancelable = cancelable;
    }

    public boolean isBusy() {
        return mCurState == ViewState.BUSY;
    }

    public boolean isBusyCancelable() {
        return mBusyCancelable;
    }

    /**
     * 初始化状态控件
     */
    private void initStateLayouts() {
        if (mRefreshLayout == null && mNormalLayout == null) {
            // 没有接入容器，则不继续
            return;
        }
        if (mRootLayout != null) {
            // 已初始化，则不再继续
            return;
        }
        // 获取上下文
        Context context = mRefreshLayout != null
                ? mRefreshLayout.getContext()
                : mNormalLayout.getContext();
        // 初始化Root容器
        mRootLayout = new FrameLayout(context);
        mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRootLayout.setClickable(true);
        mRootLayout.setFocusable(true);
        // 如果刷新容器不为空，走刷新控件逻辑
        if (mRefreshLayout != null) {
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
        } else {
            // 走常规容器逻辑
            mNormalLayout.addView(mRootLayout);
        }
    }

    /**
     * 保存刷新控件启用状态
     */
    private void saveRefreshEnableState() {
        if (mRefreshLayout != null && mCurState == ViewState.IDLE) {
            try {
                mEnableStates[0] = SMART_ENABLE_FIELDS[0].getBoolean(mRefreshLayout);
                mEnableStates[1] = SMART_ENABLE_FIELDS[1].getBoolean(mRefreshLayout);
                mEnableStates[2] = SMART_ENABLE_FIELDS[2].getBoolean(mRefreshLayout);
            } catch (IllegalAccessException ignore) {}
        }
    }

    /**
     * 恢复刷新控件启用状态
     */
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
                try {
                    SMART_ENABLE_FIELDS[3].set(mRefreshLayout, true);
                } catch (IllegalAccessException ignored) {}
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
            StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
            if (state == ViewState.BUSY) {
                adapter = strategy.getBusyAdapter() == null ? new BusyAdapter() : strategy.getBusyAdapter();
            } else if (state == ViewState.EMPTY) {
                adapter = strategy.getEmptyAdapter() == null ? new EmptyAdapter() : strategy.getEmptyAdapter();
            } else if (state == ViewState.ERROR) {
                adapter = strategy.getErrorAdapter() == null ? new ErrorAdapter() : strategy.getErrorAdapter();
            }
            mAdapters.put(state.ordinal(), adapter);
        }
        return adapter;
    }

    /**
     * 获取FragmentManager
     */
    @NonNull
    private FragmentManager getSupportFragmentManager() {
        if (mLifecycleOwner instanceof FragmentActivity) {
            return ((FragmentActivity) mLifecycleOwner).getSupportFragmentManager();
        } else {
            Fragment fragment = (Fragment) mLifecycleOwner;
            return fragment.requireActivity().getSupportFragmentManager();
        }
    }

    /**
     * 获取定位控件
     */
    @Nullable
    private View getLocationView() {
        if (mLifecycleOwner instanceof ViewProvider) {
            ViewDelegate delegate = ((ViewProvider) mLifecycleOwner).getViewDelegate();
            return delegate.getContentView();
        }
        return null;
    }
}
