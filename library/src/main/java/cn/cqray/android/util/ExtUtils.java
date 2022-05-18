package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 扩展工具类，也是一个杂乱的工具类
 * @author Cqray
 */
public class ExtUtils {

    public static boolean isEqual(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        return false;
    }

    public static boolean isEqual(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return n1 == null && n2 == null;
        }
        if (n1 instanceof Float
                || n1 instanceof Double
                || n2 instanceof Float
                || n2 instanceof Double) {
            return Math.abs(n1.doubleValue() - n2.doubleValue()) <= 0.0000001f;
        }
        return n1.longValue() == n2.longValue();
    }

    ///////////////////////////////////////////////////////
    ////////////////////   数据判空   //////////////////////
    ///////////////////////////////////////////////////////

    public static boolean isBlack(String text) {
        return isEmpty(text) || isEmpty(text.trim());
    }

    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(SparseArray<?> array) {
        return array == null || array.size() == 0;
    }

    public static boolean isEmpty(SparseIntArray array) {
        return array == null || array.size() == 0;
    }

    public static boolean isEmpty(SparseBooleanArray array) {
        return array == null || array.size() == 0;
    }

    public static boolean isEmpty(SparseLongArray array) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return array == null || array.size() == 0;
        }
        return true;
    }

    ///////////////////////////////////////////////////////
    ////////////////////   数据遍历   //////////////////////
    ///////////////////////////////////////////////////////

    public static <T> int traverse(T[] array, TraverseCallback<T> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (T item : array) {
                if (callback == null || callback.onCall(item)) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    /** 倒序遍历 **/
    public static <T> int traverseRev(T[] array, TraverseCallback<T> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = array.length -1; i >= 0; i--) {
                if (callback == null || callback.onCall(array[i])) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    public static <T> int traverse(Collection<T> collection, TraverseCallback<T> callback) {
        int count = 0;
        if (!isEmpty(collection)) {
            for (T t : collection) {
                if (callback == null || callback.onCall(t)) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    /** 倒序遍历 **/
    @SuppressWarnings("unchecked")
    public static <T> int traverseRev(Collection<T> collection, TraverseCallback<T> callback) {
        int count = 0;
        if (!isEmpty(collection)) {
            Object[] array = collection.toArray();
            for (int i = collection.size() -1; i >= 0; i--) {
                if (callback == null || callback.onCall((T) array[i])) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    public static <K, V> int traverse(Map<K, V> map, TraverseCallback<Map.Entry<K, V>> callback) {
        int count = 0;
        if (!isEmpty(map)) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (callback == null || callback.onCall(entry)) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    public static int traverse(SparseIntArray array, TraverseCallback<Integer> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = 0; i < array.size(); i++) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    /** 倒序遍历 **/
    public static int traverseRev(SparseIntArray array, TraverseCallback<Integer> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = array.size() - 1; i >= 0; i--) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    public static int traverse(SparseBooleanArray array, TraverseCallback<Boolean> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = 0; i < array.size(); i++) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    /** 倒序遍历 **/
    public static int traverseRev(SparseBooleanArray array, TraverseCallback<Boolean> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = array.size() - 1; i >= 0; i--) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int traverse(SparseLongArray array, TraverseCallback<Long> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = 0; i < array.size(); i++) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    /** 倒序遍历 **/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int traverseRev(SparseLongArray array, TraverseCallback<Long> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = array.size() - 1; i >= 0; i--) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    public static <T> int traverse(SparseArray<T> array, TraverseCallback<T> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = 0; i < array.size(); i++) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    /** 倒序遍历 **/
    public static <T> int traverseRev(SparseArray<T> array, TraverseCallback<T> callback) {
        int count = 0;
        if (!isEmpty(array)) {
            for (int i = array.size() - 1; i >= 0; i--) {
                if (callback == null || callback.onCall(array.valueAt(i))) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    ///////////////////////////////////////////////////////
    ////////////////////   控件处理   //////////////////////
    ///////////////////////////////////////////////////////

    /** 设置Margin，默认单位DP **/
    public static void setMargin(View view, float margin) {
        setMargin(view, margin, TypedValue.COMPLEX_UNIT_DIP);
    }

    public static void setMargin(View view, float margin, int unit) {
        setMargin(view, margin, margin, margin, margin, unit);
    }

    /** 设置Margin，默认单位DP **/
    public static void setMargin(View view, float left, float top, float right, float bottom) {
        setMargin(view, left, top, right, bottom, TypedValue.COMPLEX_UNIT_DIP);
    }

    public static void setMargin(View view, float left, float top, float right, float bottom, int unit) {
        if (view != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.leftMargin = (int) SizeUtils.applyDimension(left, unit);
            params.topMargin = (int) SizeUtils.applyDimension(top, unit);
            params.rightMargin = (int) SizeUtils.applyDimension(right, unit);
            params.bottomMargin = (int) SizeUtils.applyDimension(bottom, unit);
            view.requestLayout();
        }
    }

    public static void setRippleBackground(@NonNull View view, boolean rippleEnable) {
        Context context = view.getContext();
        if (rippleEnable) {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{
                        android.R.attr.actionBarItemBackground});
                drawable = ta.getDrawable(0);
                ta.recycle();
            }
            ViewCompat.setBackground(view, drawable);
        } else {
            ViewCompat.setBackground(view, null);
        }
    }

    public static void setElevation(@NonNull View view, float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevation);
        }
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            ViewCompat.setBackground(view, createMaterialShapeDrawableBackground(view.getContext(), background));
        }

        MaterialShapeUtils.setParentAbsoluteElevation(view);
        MaterialShapeUtils.setElevation(view, elevation);
    }

    public static void setOverScrollMode(View view, int overScrollMode) {
        if (view instanceof ViewPager2) {
            View child = ((ViewPager2) view).getChildAt(0);
            if (child instanceof RecyclerView) {
                child.setOverScrollMode(overScrollMode);
            }
        } else if (view != null) {
            view.setOverScrollMode(overScrollMode);
        }
    }

    @NonNull
    private static MaterialShapeDrawable createMaterialShapeDrawableBackground(Context context, Drawable background) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        if (background instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(
                    ColorStateList.valueOf(((ColorDrawable) background).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(context);
        return materialShapeDrawable;
    }

    public static void closeRvAnimator(RecyclerView rv) {
        if (rv != null) {
            RecyclerView.ItemAnimator animator = rv.getItemAnimator();
            if (animator != null) {
                animator.setAddDuration(0);
                animator.setChangeDuration(0);
                animator.setMoveDuration(0);
                animator.setRemoveDuration(0);
            }
            if (animator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            }
        }
    }

    /** 通过View获取Activity **/
    @Nullable
    public static Activity getActivity(@NonNull View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /** 渲染界面 **/
    public static View inflate(@LayoutRes int resId) {
        Activity act = ActivityUtils.getTopActivity();
        if (act != null) {
            ViewGroup root = act.findViewById(android.R.id.content);
            return LayoutInflater.from(act).inflate(resId, root, false);
        } else {
            return LayoutInflater.from(Utils.getApp()).inflate(resId, null, false);
        }
    }

    /**
     * 序列表深拷贝
     * @param serializable 序列化对象
     * @param <T> 泛型
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepClone(Serializable serializable) {
        if (serializable == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(serializable);
            //将当前这个对象写到一个输出流当中，，因为这个对象的类实现了Serializable这个接口，所以在这个类中
            //有一个引用，这个引用如果实现了序列化，那么这个也会写到这个输出流当中
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return  (T) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
            return null;
        }
    }

    ///////////////////////////////////////////////////////
    ////////////////////   数据打印   //////////////////////
    ///////////////////////////////////////////////////////

    /**
     * 用INFO日志等级打印数据
     * @param tag 日志名
     * @param prefix 前缀文字
     * @param data 数据
     */
    public static void printI(String tag, String prefix, Object data) {
        print(tag, prefix, Log.INFO, data);
    }

    /**
     * 用ERROR日志等级打印数据
     * @param tag 日志名
     * @param prefix 前缀文字
     * @param data 数据
     */
    public static void printE(String tag, String prefix, Object data) {
        print(tag, prefix, Log.ERROR, data);
    }

    /**
     * 打印数据
     * @param tag 日志名
     * @param prefix 前缀文字
     * @param level 日志等级
     * @param data 数据
     */
    public static void print(String tag, String prefix, int level, Object data) {
        StringBuilder sb = new StringBuilder();
        if (data == null) {
            return;
        }
        int count = 0;
        if (data.getClass().isArray()) {
            count = traverse((Object[]) data, generateCallback(sb));
        } else if (data instanceof Collection) {
            count = traverse((Collection<?>) data, generateCallback(sb));
        } else if (data instanceof SparseIntArray) {
            count = traverse((SparseIntArray) data, generateCallback(sb));
        } else if (data instanceof SparseBooleanArray) {
            count = traverse((SparseBooleanArray) data, generateCallback(sb));
        } else if (data instanceof SparseArray) {
            count = traverse((SparseArray<?>) data, generateCallback(sb));
        } else if (data instanceof Map) {
            count = traverse((Map<?, ?>) data, item -> {
                sb.append("[K => ")
                        .append(item.getKey())
                        .append(", V => ")
                        .append(item.getValue())
                        .append("], ");
                return false;
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (data instanceof SparseLongArray) {
                    count = traverse((SparseLongArray) data, generateCallback(sb));
                }
            }
        }
        sb.setLength(count > 0 ? sb.length() - 2 : 0);
        Log.println(level, tag, prefix == null ? sb.toString() : prefix + sb.toString());
    }

    @NonNull
    private static <T> TraverseCallback<T> generateCallback(@NonNull StringBuilder sb) {
        return item -> {
            sb.append(item.toString()).append(", ");
            return false;
        };
    }

    /**
     * 遍历回调
     */
    public interface TraverseCallback<T> {

        /**
         * 遍历子项回调
         * @param item 子项
         * @return 是否拦截
         */
        boolean onCall(T item);
    }
}
