package cn.cqray.android.tip;

/**
 * 提示操作提供者
 * @author Cqray
 * @date 2022/3/12
 */
public interface TipProvider {

    TipDelegate getTipDelegate();

    void showInfo(String text);

    void showInfo(String text, int duration);

    void showWarning(String text);

    void showWarning(String text, int duration);

    void showError(String text);

    void showError(String text, int duration);

    void showSuccess(String text);

    void showSuccess(String text, int duration);
}
