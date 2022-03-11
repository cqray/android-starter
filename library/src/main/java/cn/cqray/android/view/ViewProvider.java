package cn.cqray.android.view;

public interface ViewProvider {

    ViewDelegate getViewDelegate();

    void setIdle();

    void setBusy(String ...texts);

    void setEmpty(String ...texts);

    void setError(String ...texts);
}
