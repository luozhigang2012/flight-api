package com.example.flightapi.controller;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.dto.auth.AuthResponseDTO;
import com.example.flightapi.dto.auth.LoginRequestDTO;
import com.example.flightapi.dto.auth.RegisterRequestDTO;
import com.example.flightapi.service.AuthService;
import com.example.flightapi.util.ApplicationContextProvider;
import com.example.flightapi.util.LocaleUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 处理用户认证相关的HTTP请求，包括注册、登录、注销和认证状态检查。
 * 支持国际化消息响应。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * 用户注册端点
     * <p>
     * 处理新用户注册请求，创建用户账户并返回JWT令牌。
     * </p>
     *
     * @param request 包含用户注册信息的请求体
     * @param httpRequest HTTP请求对象，用于获取语言参数
     * @return 包含JWT令牌和用户信息的响应
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
            @RequestBody @Valid RegisterRequestDTO request,
            HttpServletRequest httpRequest) {
        
        // 获取语言参数
        String lang = httpRequest.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Processing registration request for email: {}, language: {}",
                request.getEmail(), lang);
        
        ResponseEntity<ApiResponse<AuthResponseDTO>> response = authService.register(request);
        
        // 如果需要，可以在这里修改响应消息
        if (response.getStatusCode().is2xxSuccessful()) {
            ApiResponse<AuthResponseDTO> body = response.getBody();
            if (body != null) {
                // 使用国际化消息
                String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                        .getMessage("auth.register.success", null, locale);
                body.setMessage(successMessage);
            }
        }
        
        return response;
    }

    /**
     * 用户登录端点
     * <p>
     * 验证用户凭证并返回JWT令牌。
     * </p>
     *
     * @param request 包含登录凭证的请求体
     * @param httpRequest HTTP请求对象，用于获取语言参数
     * @return 包含JWT令牌和用户信息的响应
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @RequestBody @Valid LoginRequestDTO request,
            HttpServletRequest httpRequest) {
        
        // 获取语言参数
        String lang = httpRequest.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        // 避免在日志中暴露敏感信息
        log.info("Processing login request, language: {}", lang);
        
        ResponseEntity<ApiResponse<AuthResponseDTO>> response = authService.login(request);
        
        // 如果需要，可以在这里修改响应消息
        if (response.getStatusCode().is2xxSuccessful()) {
            ApiResponse<AuthResponseDTO> body = response.getBody();
            if (body != null) {
                // 使用国际化消息
                String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                        .getMessage("auth.login.success", null, locale);
                body.setMessage(successMessage);
            }
        }
        
        return response;
    }

    /**
     * 用户注销端点
     * <p>
     * 使当前JWT令牌失效，实现用户注销功能。
     * </p>
     *
     * @param authHeader 包含Bearer令牌的认证头
     * @param httpRequest HTTP请求对象，用于获取语言参数
     * @return 注销操作的结果
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest) {
        
        // 获取语言参数
        String lang = httpRequest.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Processing logout request, language: {}", lang);
        
        ResponseEntity<ApiResponse<Void>> response = authService.logout(authHeader);
        
        // 如果需要，可以在这里修改响应消息
        if (response.getStatusCode().is2xxSuccessful()) {
            ApiResponse<Void> body = response.getBody();
            if (body != null) {
                // 使用国际化消息
                String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                        .getMessage("auth.logout.success", null, locale);
                body.setMessage(successMessage);
            }
        }
        
        return response;
    }

    /**
     * 认证状态检查端点
     * <p>
     * 验证当前JWT令牌是否有效。
     * </p>
     *
     * @param authHeader 包含Bearer令牌的认证头
     * @param request HTTP请求对象，用于获取语言参数
     * @return 包含认证状态的响应
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> check(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {
        
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Checking authentication status, language: {}", lang);
        
        boolean loggedIn = authService.check(authHeader);
        
        // 使用国际化消息
        String message = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage(loggedIn ? "auth.check.authenticated" : "auth.check.unauthenticated", null, locale);
        
        return ResponseEntity.ok(ApiResponse.success(200, message, loggedIn));
    }
    
    /**
     * 刷新令牌端点
     * <p>
     * 使用有效的刷新令牌生成新的访问令牌。
     * </p>
     *
     * @param requestBody 包含刷新令牌的请求体
     * @param httpRequest HTTP请求对象，用于获取语言参数
     * @return 包含新访问令牌的响应
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest httpRequest) {
        
        // 获取语言参数
        String lang = httpRequest.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Processing token refresh request, language: {}", lang);
        
        // 从请求体中获取刷新令牌
        String refreshToken = requestBody.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Token refresh failed: Missing refresh token");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<AuthResponseDTO>error(400, "Refresh token is required"));
        }
        
        ResponseEntity<ApiResponse<AuthResponseDTO>> response = authService.refreshToken(refreshToken);
        
        // 如果需要，可以在这里修改响应消息
        if (response.getStatusCode().is2xxSuccessful()) {
            ApiResponse<AuthResponseDTO> body = response.getBody();
            if (body != null) {
                // 使用国际化消息
                String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                        .getMessage("auth.token.refreshed", null, locale);
                body.setMessage(successMessage);
            }
        }
        
        return response;
    }
}
