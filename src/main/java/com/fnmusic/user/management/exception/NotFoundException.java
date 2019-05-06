package com.fnmusic.user.management.exception;

public class NotFoundException extends AbstractException {

    public NotFoundException(int code, String message) {
        super(code, message);
    }
}
