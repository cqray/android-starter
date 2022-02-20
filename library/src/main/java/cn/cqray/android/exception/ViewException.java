package cn.cqray.android.exception;

import java.io.Serializable;

/**
 * @author Admin
 */
public class ViewException extends RuntimeException implements Serializable {

    private String mMessage;

    public ViewException(String message) {
        mMessage = message;
    }
}
