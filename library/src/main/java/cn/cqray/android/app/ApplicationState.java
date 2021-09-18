package cn.cqray.android.app;

/**
 * 应用状态
 * @author Cqray
 */
public enum ApplicationState {
    /** 初始化中 **/ INITIALIZE,
    /** 后台 **/ BACKGROUND,
    /** 前台 **/ FOREGROUND,
    /** 退出中 **/ EXITED
}
