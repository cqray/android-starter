package cn.cqray.android.tip;

public interface TipProvider {

    void showInfo(String text);

    void showInfo(String text, int duration);

    void showSuccess(String text);

    void showSuccess(String text, int duration);

    void showFailure(String text);

    void showFailure(String text, int duration);
}
