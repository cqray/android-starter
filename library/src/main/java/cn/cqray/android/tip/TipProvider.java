package cn.cqray.android.tip;

/**
 * 提示操作提供者
 * @author Cqray
 * @date 2022/3/12
 */
public interface TipProvider {

    /**
     * 获取提示操作委托
     * @return TipDelegate
     */
    TipDelegate getTipDelegate();
}
