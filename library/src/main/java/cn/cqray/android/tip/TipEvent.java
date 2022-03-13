package cn.cqray.android.tip;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 提示事件
 * @author Cqray
 * @date 2022/3/12
 */
@Data
@AllArgsConstructor
class TipEvent {

    /** 提示等级 **/
    private TipLevel level;
    /** 提示文本 **/
    private String text;
    /** 显示时长 **/
    private int duration;

    public TipEvent(TipLevel level, String text) {
        this.level = level;
        this.text = text;
        this.duration = 1500;
    }
}
