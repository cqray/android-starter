package cn.cqray.java;

import android.os.Build;
import android.util.SparseLongArray;

import cn.cqray.java.traverse.TraverseCallback;
import cn.cqray.java.type.TypeAdapter;

/**
 * SparseLongArray类型适配器
 * @author Cqray
 */
public class SparseLongArrayTypeAdapter extends TypeAdapter<SparseLongArray> {

    @Override
    public Class<SparseLongArray> getTypeClass() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return SparseLongArray.class;
        }
        throw new UnsupportedOperationException("Class requires API level 18 : android.util.SparseLongArray ");
    }

    @Override
    public int size(SparseLongArray data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return data == null ? 0 : data.size();
        }
        return 0;
    }

    @Override
    public void onTraversal(SparseLongArray data, boolean key, boolean reserve, TraverseCallback<Object> callback) {
        if (data == null || callback == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return;
        }
        if (reserve) {
            for (int i = data.size() - 1; i >= 0; i--) {
                Object val = key ? data.keyAt(i) : data.valueAt(i);
                if (callback.onCall(val)) {
                    break;
                }
            }
        } else {
            for (int i = 0; i < data.size(); i++) {
                Object val = key ? data.keyAt(i) : data.valueAt(i);
                if (callback.onCall(val)) {
                    break;
                }
            }
        }
    }
}
