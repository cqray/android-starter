package cn.cqray.android.strategy;

import cn.cqray.android.state.BusyAdapter;
import cn.cqray.android.state.EmptyAdapter;
import cn.cqray.android.state.ErrorAdapter;
import cn.cqray.android.state.StateAdapter;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 状态显示配置策略
 * @author Cqray
 */
@Getter
@Builder
@Accessors(prefix = "m")
public class StateStrategy {
    private String mEmptyText;
    private String mErrorText;
    private @Builder.Default StateAdapter emptyAdapter = new EmptyAdapter();
    private @Builder.Default StateAdapter errorAdapter = new ErrorAdapter();
    private @Builder.Default StateAdapter busyAdapter = new BusyAdapter();
}
