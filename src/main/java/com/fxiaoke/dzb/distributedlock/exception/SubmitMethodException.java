package com.fxiaoke.dzb.distributedlock.exception;

/**
 * @author: dongzhb
 * @date: 2019/5/10
 * @Description:
 */
public class SubmitMethodException extends RuntimeException {
    public SubmitMethodException() {
        super();
    }

    public SubmitMethodException(String message) {
        super(message);
    }

    public SubmitMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
