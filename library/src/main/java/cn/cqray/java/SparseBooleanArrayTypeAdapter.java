package cn.cqray.java;

import android.util.SparseBooleanArray;

import cn.cqray.java.traverse.TraverseCallback;
import cn.cqray.java.type.TypeAdapter;

/**
 * SparseBooleanArray类型适配器
 * @author Cqray
 */
public class SparseBooleanArrayTypeAdapter extends TypeAdapter<SparseBooleanArray> {

    @Override
    public Class<SparseBooleanArray> getTypeClass() {
        return SparseBooleanArray.class;
    }

    @Override
    public int size(SparseBooleanArray data) {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onTraversal(SparseBooleanArray data, boolean key, boolean reserve, TraverseCallback<Object> callback) {
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
