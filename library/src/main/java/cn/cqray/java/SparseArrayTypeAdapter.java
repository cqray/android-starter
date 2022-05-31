package cn.cqray.java;

import android.util.SparseArray;

import cn.cqray.java.traverse.TraverseCallback;
import cn.cqray.java.type.TypeAdapter;

/**
 * SparseArray类型适配器
 * @author Cqray
 */
public class SparseArrayTypeAdapter extends TypeAdapter<SparseArray> {

    @Override
    public Class<SparseArray> getTypeClass() {
        return SparseArray.class;
    }

    @Override
    public int size(SparseArray data) {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onTraversal(SparseArray data, boolean key, boolean reserve, TraverseCallback<Object> callback) {
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
