package com.example.flightapi.exception;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String messageKey) {
        super(messageKey);
    }
    
    public ResourceNotFoundException(String messageKey, Object[] args) {
        super(messageKey, args);
    }
}