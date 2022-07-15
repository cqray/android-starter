package cn.cqray.android.state;

import android.content.Context;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.SizeUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.R;
import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.util.ContextUtils;

/**
 * 状态管理委托
 * @author Cqray
 */
public class StateDelegate implements Serializable {

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

    /** 忙碌状态是否可取消 **/
    private boolean mBusyCancelable;
    /** 父容器 **/
    private SmartRefreshLayout mRefreshLayout;
    /** 常规界面 **/
    private FrameLayout mNormalLayout;
    /** 状态根布局 **/
    private FrameLayout mRootLayout;
    /** 当前状态 **/
    private ViewState mCurState;
    /** 偏移量 **/
    private final MutableLiveData<float[]> mOffsets = new MutableLiveData<>();
    /** 状态缓存 **/
    private final Boolean[] mEnableStates = new Boolean[3];
    /** 适配器集合 **/
    private final SparseArray<StateAdapter<?>> mAdapters = new SparseArray<>();
    /** LifecycleOwner对象 **/
    private final LifecycleOwner mLifecycleOwner;

    public StateDelegate(FragmentActivity activity) {
        mLifecycleOwner = activity;
        // 忙碌状态是否可取消
        mBusyCancelable = Starter.getInstance().getStarterStrategy().isBusyCancelable();
        // 设置容器偏移
        mOffsets.observe(activity, this::setRootLayoutOffsets);
    }

    public StateDelegate(Fragment fragment) {
        mLifecycleOwner = fragment;
        // 忙碌状态是否可取消
        mBusyCancelable = Starter.getInstance().getStarterStrategy().isBusyCancelable();
        // 设置容器偏移
        mOffsets.observe(fragment, this::setRootLayoutOffsets);
    }

    public void attachLayout(FrameLayout layout) {
        // 取消常规状态容器关联
        detachLayout(mRefreshLayout);
        // 更新刷新状态容器
        mRefreshLayout = layout == null ? mRefreshLayout : null;
        mNormalLayout = layout;
    }

    public void attachLayout(SmartRefreshLayout layout) {
        // 取消常规状态容器关联
        detachLayout(mNormalLayout);
        // 更新常规状态容器
        mNormalLayout = layout == null ? mNormalLayout : null;
        mRefreshLayout = layout;
    }

    void detachLayout(ViewGroup layout) {
        if (mRootLayout != null) {
            setIdle();
            if (layout instanceof SmartRefreshLayout) {
                SmartRefreshLayout refreshLayout = (SmartRefreshLayout) layout;
                FrameLayout content = (FrameLayout) mRootLayout.getParent();
                if (content != null) {
                    // 移除根容器
                    content.removeView(mRootLayout);
                    if (content.getChildCount() == 1) {
                        // 如果内容容器只剩下一个控件
                        View view = content.getChildAt(0);
                        content.removeView(view);
                        // 将那个控件直接加入刷新控件
                        refreshLayout.removeView(content);
                        refreshLayout.setRefreshContent(view);
                    }
                }
            } else if (layout instanceof FrameLayout) {
                layout.removeView(mRootLayout);
            }
        }
    }

    public void setIdle() {
        setState(ViewState.IDLE);
    }

    public void setBusy(String... texts) {
        setState(ViewState.BUSY, texts);
    }

    public void setEmpty(String... texts) {
        setState(ViewState.EMPTY, texts);
    }

    public void setError(String... texts) {
        setState(ViewState.ERROR, texts);
    }

    public synchronized void setState(ViewState state, String... texts) {
        String text = convert2Text(texts);
        // 保存刷新控件空闲状态时的相关属性
        saveRefreshEnableState();
        // 更新当前状态
        mCurState = state;
        // 初始化控件
        initStateLayouts();
        // 隐藏所有状态控件
        for (int i = 0; i < mAdapters.size(); i++) {
            StateAdapter<?> adapter = mAdapters.valueAt(i);
            if (adapter != null && state != ViewState.BUSY) {
                // 忙碌状态时，之前界面可能为空或异常，所以不隐藏
                adapter.hide();
            }
        }
        // 显示对应的状态控件
        StateAdapter<?> adapter = getAdapter(state);
        if (adapter != null) {
            adapter.show(text);
            if (adapter.mContentView == null) {
                adapter.onAttach(this, mRootLayout);
            }
        }
        // 恢复刷新控件状态
        restoreRefreshEnableState();
    }

    public void setOffsetTop(float offset) {
        setOffsetTop(offset, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setOffsetTop(float offset, int unit) {
        mOffsets.setValue(new float[] {0, SizeUtils.applyDimension(offset, unit), 0, 0});
    }

    public void setOffsetBottom(float offset) {
        setOffsetBottom(offset, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setOffsetBottom(float offset, int unit) {
        mOffsets.setValue(new float[] {0, 0, 0, SizeUtils.applyDimension(offset, unit)});
    }

    public void setOffsets(float left, float top, float right, float bottom) {
        setOffsets(left, top, right, bottom, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setOffsets(float left, float top, float right, float bottom, int unit) {
        mOffsets.setValue(new float[] {
                SizeUtils.applyDimension(left, unit),
                SizeUtils.applyDimension(top, unit),
                SizeUtils.applyDimension(right, unit),
                SizeUtils.applyDimension(bottom, unit)
        });
    }

    public void setBusyAdapter(StateAdapter<?> adapter) {
        setStateAdapter(ViewState.BUSY, adapter);
    }

    public void setEmptyAdapter(StateAdapter<?> adapter) {
        setStateAdapter(ViewState.EMPTY, adapter);
    }

    public void setErrorAdapter(StateAdapter<?> adapter) {
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

    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    public ViewState getCurrentState() {
        return mCurState;
    }

    /**
     * 设置关联容器的偏移数据
     */
    void setRootLayoutOffsets() {
        ViewGroup parent = (ViewGroup) mRootLayout.getParent();
        mRootLayout.setVisibility(View.GONE);
        mRootLayout.post(() -> {
            // 如果标记了内容控件，则有标题栏存在
            View toolbar = parent.findViewById(R.id.starter_toolbar);
            float[] offsets = mOffsets.getValue() == null ? new float[4] : mOffsets.getValue();
            int top = (int) offsets[1];
            if (top == 0 && toolbar != null) {
                // 获取标题栏底部位置为偏移量
                top = toolbar.getBottom();
            }
            mRootLayout.setVisibility(mCurState == ViewState.IDLE ? View.GONE : View.VISIBLE);
            // 设置四周的间距
            setRootLayoutOffsets(new float[] {offsets[0], top, offsets[2], offsets[3]});
        });
    }

    void setRootLayoutOffsets(@NonNull float[] offsets) {
        // 设置四周的间距
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRootLayout.getLayoutParams();
        params.setMargins((int) offsets[0], (int) offsets[1], (int) offsets[2], (int) offsets[3]);
        mRootLayout.requestLayout();
    }

    @Nullable
    private String convert2Text(String... texts) {
        // 无数据
        if (texts == null || texts.length == 0) {
            return null;
        }
        // 单个数据
        if (texts.length == 1) {
            return texts[0];
        }
        // 多个数据
        StringBuilder builder = new StringBuilder();
        for (String text : texts) {
            builder.append(text).append("\n");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    /**
     * 初始化状态控件
     */
    private void initStateLayouts() {
        if (mRefreshLayout == null && mNormalLayout == null) {
            // 没有接入容器，则不继续
            return;
        }
        if (mRootLayout != null && mRootLayout.getParent() != null) {
            return;
        }
        // 获取上下文
        Context context = ContextUtils.get();
        // 初始化Root容器
        if (mRootLayout == null) {
            mRootLayout = new FrameLayout(context);
            mRootLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            mRootLayout.setClickable(true);
            mRootLayout.setFocusable(true);
        }
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
        // 设置容器偏移
        setRootLayoutOffsets();
    }

    /**
     * 保存刷新控件空闲状态时的相关属性
     */
    private void saveRefreshEnableState() {
        if (mRefreshLayout != null) {
            if (mCurState == null || mCurState == ViewState.IDLE) {
                try {
                    mEnableStates[0] = SMART_ENABLE_FIELDS[0].getBoolean(mRefreshLayout);
                    mEnableStates[1] = SMART_ENABLE_FIELDS[1].getBoolean(mRefreshLayout);
                    mEnableStates[2] = SMART_ENABLE_FIELDS[2].getBoolean(mRefreshLayout);
                } catch (IllegalAccessException ignore) {}
            }
        }
    }

    /**
     * 恢复刷新控件启用状态
     */
    private void restoreRefreshEnableState() {
        if (mRefreshLayout != null) {
            if (mCurState == ViewState.IDLE) {
                try {
                    SMART_ENABLE_FIELDS[0].setBoolean(mRefreshLayout, mEnableStates[0]);
                    SMART_ENABLE_FIELDS[1].setBoolean(mRefreshLayout, mEnableStates[1]);
                    SMART_ENABLE_FIELDS[2].setBoolean(mRefreshLayout, mEnableStates[2]);
                } catch (IllegalAccessException ignore) {}
            } else {
                try {
                    SMART_ENABLE_FIELDS[0].setBoolean(mRefreshLayout, mCurState != ViewState.BUSY && mEnableStates[0]);
                    SMART_ENABLE_FIELDS[1].setBoolean(mRefreshLayout, false);
                    SMART_ENABLE_FIELDS[2].setBoolean(mRefreshLayout, mCurState != ViewState.BUSY);
                    SMART_ENABLE_FIELDS[3].setBoolean(mRefreshLayout, true);
                } catch (IllegalAccessException ignore) {}
            }
        }
    }

    /**
     * 设置对应的状态适配器
     * @param state 状态
     * @param adapter 适配器
     */
    private void setStateAdapter(@NonNull ViewState state, StateAdapter<?> adapter) {
        StateAdapter<?> sAdapter = mAdapters.get(state.ordinal());
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
    private StateAdapter<?> getAdapter(@NonNull ViewState state) {
        StateAdapter<?> adapter = mAdapters.get(state.ordinal());
        if (adapter == null) {
            // 获取全局状态适配器
            StarterStrategy strategy = Starter.getInstance().getStarterStrategy();
            if (state == ViewState.BUSY) {
                adapter = strategy.getBusyAdapter();
            } else if (state == ViewState.EMPTY) {
                adapter = strategy.getEmptyAdapter();
            } else if (state == ViewState.ERROR) {
                adapter = strategy.getErrorAdapter();
            }
            if (adapter != null) {
                // 因为需要关联布局，所以需要克隆适配器
                adapter = adapter.deepClone();
                if (adapter != null) {
                    adapter.hide();
                }
            }
            mAdapters.put(state.ordinal(), adapter);
        }
        return adapter;
    }

}
