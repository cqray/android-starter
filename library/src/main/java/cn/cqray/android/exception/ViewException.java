package cn.cqray.android.exception;

import java.io.Serializable;

/**
 * @author Admin
 * @date 2021/9/15 9:48
 */
public class ViewException extends RuntimeException implements Serializable {

    private String mMessage;

    public ViewException(String message) {
        mMessage = message;
    }
}
