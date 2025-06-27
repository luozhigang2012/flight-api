package com.example.flightapi.exception;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.util.LocaleUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        
        log.error("Unauthorized access: {}", ex.getMessageKey());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, ex.getLocalizedMessage(lang)));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        
        log.error("Business rule violation: {}", ex.getMessageKey());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getLocalizedMessage(lang)));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        
        log.error("Resource not found: {}", ex.getMessageKey());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getLocalizedMessage(lang)));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.error("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, messageSource.getMessage("entity.not.found", null, locale)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        // 获取所有验证错误
        var allErrors = ex.getBindingResult().getAllErrors();
        
        // 如果没有错误，返回通用验证错误消息
        if (allErrors.isEmpty()) {
            String genericMsg = messageSource.getMessage("validation.error", null, locale);
            log.error("Validation error with no specific details");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, genericMsg));
        }
        
        // 获取第一个错误作为主要错误消息
        var fieldError = ex.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : "";
        String msg = fieldError != null ? fieldError.getDefaultMessage() :
                     messageSource.getMessage("validation.error", null, locale);
        
        // 添加更详细的日志
        log.error("Validation error on field {}: {}", field, msg);
        
        // 记录所有验证错误
        allErrors.forEach(error -> {
            String errorField = error instanceof org.springframework.validation.FieldError ?
                               ((org.springframework.validation.FieldError) error).getField() : "unknown";
            log.debug("Field '{}' error: {}", errorField, error.getDefaultMessage());
        });
        
        log.debug("Request path: {}", request.getRequestURI());
        
        // 返回第一个错误作为响应
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        // 记录详细错误日志（英文）
        log.error("Internal server error", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, messageSource.getMessage("internal.error", null, locale)));
    }
}
