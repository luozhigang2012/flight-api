package com.example.flightapi.exception;

public class BusinessRuleException extends BaseException {
    public BusinessRuleException(String messageKey) {
        super(messageKey);
    }
    
    public BusinessRuleException(String messageKey, Object[] args) {
        super(messageKey, args);
    }
}