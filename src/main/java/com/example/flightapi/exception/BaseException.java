package com.example.flightapi.exception;

import com.example.flightapi.util.ApplicationContextProvider;
import com.example.flightapi.util.LocaleUtils;
import org.springframework.context.MessageSource;

import java.util.Locale;

public abstract class BaseException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;
    
    public BaseException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = null;
    }
    
    public BaseException(String messageKey, Object[] args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
    
    public String getMessageKey() {
        return messageKey;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public String getLocalizedMessage(String lang) {
        MessageSource messageSource = ApplicationContextProvider.getBean(MessageSource.class);
        Locale locale = LocaleUtils.parseLocale(lang);
        return messageSource.getMessage(messageKey, args, locale);
    }
}