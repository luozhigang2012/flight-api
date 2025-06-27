package com.example.flightapi.util;

import com.example.flightapi.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * JWT认证过滤器
 * <p>
 * 该过滤器拦截所有HTTP请求，检查请求头中是否包含有效的JWT令牌。
 * 如果令牌有效，则设置Spring Security上下文，允许请求继续处理。
 * 如果令牌无效或不存在，则请求将继续传递给过滤器链，由后续的安全配置决定是否允许访问。
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * 处理每个HTTP请求，验证JWT令牌并设置安全上下文
     *
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. 从请求头中获取Authorization
            final String authHeader = request.getHeader("Authorization");
            
            // 2. 如果Authorization头不存在或不是Bearer类型，则跳过处理
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // 3. 提取JWT令牌（去掉"Bearer "前缀）
            final String jwt = authHeader.substring(7);
            
            // 4. 验证令牌类型和提取用户名
            final String username;
            try {
                // 验证令牌类型，确保只处理访问令牌
                String tokenType = jwtUtil.extractClaim(jwt, claims -> claims.get("type", String.class));
                if (!"access".equals(tokenType)) {
                    log.warn("Request contains non-access type token");
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token type");
                    return;
                }
                
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.warn("Request contains expired JWT token");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            } catch (MalformedJwtException | UnsupportedJwtException e) {
                log.warn("Request contains invalid JWT token");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token format");
                return;
            } catch (JwtException e) {
                log.warn("JWT token validation failed");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
                return;
            }
            
            // 5. 如果成功提取用户名且当前没有认证信息，则验证令牌
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // 6. 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 7. 验证令牌是否有效
                    if (jwtUtil.isTokenValid(jwt, userDetails)) {
                        // 8. 创建认证令牌并设置到安全上下文
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        // 避免在日志中暴露完整的用户名
                        String maskedUsername = maskUsername(username);
                        log.debug("User '{}' authenticated successfully", maskedUsername);
                    } else {
                        // 避免在日志中暴露完整的用户名
                        String maskedUsername = maskUsername(username);
                        log.debug("Invalid token for user: '{}'", maskedUsername);
                    }
                } catch (UsernameNotFoundException e) {
                    // 避免在日志中暴露完整的用户名
                    String maskedUsername = maskUsername(username);
                    log.warn("User in JWT token does not exist: {}", maskedUsername);
                }
            }
            
            // 9. 继续过滤器链
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 10. 捕获所有未处理的异常
            log.error("Unhandled exception occurred during JWT authentication", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    /**
     * 发送错误响应
     *
     * @param response   HTTP响应
     * @param statusCode HTTP状态码
     * @param message    错误消息
     * @throws IOException IO异常
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        
        // 使用ApiResponse创建标准格式的错误响应（使过滤器层的错误响应与控制器层保持一致，都使用相同的 ApiResponse 格式）
        ApiResponse<Void> errorResponse = ApiResponse.error(statusCode, message);
        
        // 使用ObjectMapper将ApiResponse对象转换为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
    
    /**
     * 对用户名进行掩码处理，以避免在日志中暴露完整的用户名
     * 例如：user@example.com -> u***@e***.com
     *
     * @param username 原始用户名
     * @return 掩码处理后的用户名
     */
    private String maskUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "unknown";
        }
        
        // 如果是邮箱地址
        if (username.contains("@")) {
            String[] parts = username.split("@");
            if (parts.length == 2) {
                String name = parts[0];
                String domain = parts[1];
                
                // 掩码处理用户名部分
                String maskedName = name.length() > 1
                    ? name.substring(0, 1) + "***"
                    : name;
                
                // 掩码处理域名部分
                String maskedDomain;
                if (domain.contains(".")) {
                    String[] domainParts = domain.split("\\.", 2);
                    maskedDomain = domainParts[0].length() > 1
                        ? domainParts[0].substring(0, 1) + "***"
                        : domainParts[0];
                    maskedDomain += "." + domainParts[1];
                } else {
                    maskedDomain = domain.length() > 1
                        ? domain.substring(0, 1) + "***"
                        : domain;
                }
                
                return maskedName + "@" + maskedDomain;
            }
        }
        
        // 非邮箱地址
        if (username.length() <= 2) {
            return username;
        } else {
            return username.substring(0, 1) + "***" +
                   (username.length() > 3 ? username.substring(username.length() - 1) : "");
        }
    }
}
