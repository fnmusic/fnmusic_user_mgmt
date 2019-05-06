package com.fnmusic.user.management.exception;

public class InternalServerErrorException extends Exception {

    private int statusCode;

    public InternalServerErrorException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatisCode() {
        return statusCode;
    }

    public void setStatisCode(int statisCode) {
        this.statusCode = statisCode;
    }
}
