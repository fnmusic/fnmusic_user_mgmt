package com.fnmusic.user.management.exception;

public class AbstractException extends RuntimeException {

    protected static final Long serialVersionUID = 1L;
    int code;

    public AbstractException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AbstractException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
