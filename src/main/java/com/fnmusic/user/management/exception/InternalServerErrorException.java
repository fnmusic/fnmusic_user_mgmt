package com.fnmusic.user.management.exception;

public class InternalServerErrorException extends AbstractException {

    private int statusCode;

    public InternalServerErrorException(String message) {
        super(message);
    }

}
