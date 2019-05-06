package com.fnmusic.user.management.exception;

public class AlreadyExistsException extends Exception {

    private int statusCode;

    public AlreadyExistsException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
