package cn.cqray.android.swipe;

import androidx.customview.widget.ViewDragHelper;

/**
 * 边缘滑动方向
 * @author Cqray
 */
public enum EdgeOrientation {

    /** 左侧滑动 **/ LEFT(ViewDragHelper.EDGE_LEFT),
    /** 右侧滑动 **/ RIGHT(ViewDragHelper.EDGE_RIGHT),
    /** 上方滑动 **/ TOP(ViewDragHelper.EDGE_TOP),
    /** 下方滑动 **/ BOTTOM(ViewDragHelper.EDGE_BOTTOM),
    /** 横向滑动 **/ HORIZONTAL(ViewDragHelper.EDGE_LEFT | ViewDragHelper.EDGE_RIGHT ),
    /** 纵向滑动 **/ VERTICAL(ViewDragHelper.EDGE_TOP | ViewDragHelper.EDGE_BOTTOM),
    /** 全方位滑动 **/ ALL(ViewDragHelper.EDGE_ALL);

    int mCode;

    EdgeOrientation(int code) {
        mCode = code;
    }

    static EdgeOrientation find(int flag) {
        for (EdgeOrientation eo : values()) {
            if (eo.mCode == flag) {
                return eo;
            }
        }
        return LEFT;
    }
}
