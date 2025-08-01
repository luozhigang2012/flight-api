package com.example.flightapi.config;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@Profile("dev") // 只在开发环境激活
@Slf4j
public class ApiLoggingAspect {

    private final ObjectMapper objectMapper;

    private final MessageSource messageSource;

    public ApiLoggingAspect(MessageSource messageSource) {
        this.objectMapper = new ObjectMapper();
        this.messageSource = messageSource;
    }

    // 定义切入点，匹配所有Controller包下的所有公共方法
    @Pointcut("execution(public * com.example.flightapi.controller.*.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            // 如果不是HTTP请求上下文，例如单元测试，则跳过
            log.warn("Not an HTTP request context, skipping API logging aspect.");
            return joinPoint.proceed();
        }

        String methodName = joinPoint.getSignature().toShortString();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        Object[] args = joinPoint.getArgs();

        // 记录请求信息
        log.info("-----> API Request: {} {} {}", method, requestURI, (queryString != null ? "?" + queryString : ""));
        log.info("       Method: {}", methodName);
        log.info("       Headers: {}", getRequestHeaders(request));
        log.info("       Parameters: {}", getRequestParameters(request));
        log.info("       Arguments: {}", Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed(); // 执行目标方法
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录正常响应
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                log.info("<----- API Response: {} {} ({}ms) Status: {}", method, requestURI, duration, responseEntity.getStatusCode().value());
                if (responseEntity.hasBody()) {
                    // 尝试获取ApiResponse的data部分，避免日志过长
                    Object body = responseEntity.getBody();
                    if (body instanceof ApiResponse) {
                        ApiResponse<?> apiResponse = (ApiResponse<?>) body;
                        try {
                            log.info("       Response Body: {}", objectMapper.writeValueAsString(apiResponse));
                        } catch (Exception e) {
                            log.error("Error converting ApiResponse to JSON: {}", e.getMessage());
                            log.info("       Response Body: {}", apiResponse); // Fallback to default toString()
                        }
                    } else {
                        try {
                            log.info("       Response Body: {}", objectMapper.writeValueAsString(body));
                        } catch (Exception e) {
                            log.error("Error converting response body to JSON: {}", e.getMessage());
                            log.info("       Response Body: {}", body); // Fallback to default toString()
                        }
                    }
                }
            } else {
                log.info("<----- API Response: {} {} ({}ms) Result: {}", method, requestURI, duration, result);
            }
        } catch (ResourceNotFoundException ex) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            // 对于业务异常，只记录错误信息，不打印堆栈
            log.warn("<----- API Business Exception: {} {} ({}ms) Warning: {}. Details: {}",
                    method, requestURI, duration, ex.getMessage(),
                    messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), LocaleContextHolder.getLocale()));
            throw ex; // 重新抛出异常，让GlobalExceptionHandler处理
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            // 记录异常响应
            log.error("<----- API Exception: {} {} ({}ms) Error: {}", method, requestURI, duration, e.getMessage(), e);
            throw e; // 重新抛出异常，让GlobalExceptionHandler处理
        }
        return result;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private String getRequestParameters(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining("&"));
    }
}