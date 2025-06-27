package com.example.flightapi.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String messageKey) {
        super(messageKey);
    }
}