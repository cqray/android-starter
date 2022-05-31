package cn.cqray.java;

import android.util.SparseIntArray;

import cn.cqray.java.traverse.TraverseCallback;
import cn.cqray.java.type.TypeAdapter;

/**
 * SparseIntArray类型适配器
 * @author Cqray
 */
public class SparseIntArrayTypeAdapter extends TypeAdapter<SparseIntArray> {

    @Override
    public Class<SparseIntArray> getTypeClass() {
        return SparseIntArray.class;
    }

    @Override
    public int size(SparseIntArray data) {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onTraversal(SparseIntArray data, boolean key, boolean reserve, TraverseCallback<Object> callback) {
        if (data == null || callback == null) {
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
