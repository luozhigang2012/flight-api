package com.example.flightapi.service;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.dto.auth.AuthResponseDTO;
import com.example.flightapi.dto.auth.LoginRequestDTO;
import com.example.flightapi.dto.auth.RegisterRequestDTO;
import com.example.flightapi.dto.user.UserInfoDTO;
import com.example.flightapi.entity.User;
import com.example.flightapi.repository.UserRepository;
import com.example.flightapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务类
 * <p>
 * 处理用户注册、登录、令牌验证和注销功能。
 * 管理JWT令牌的生成和验证，以及令牌撤销。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    // 使用ConcurrentHashMap存储已撤销的令牌和请求计数
    // 在生产环境中，应该使用Redis或其他分布式缓存来存储
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    
    // 用于实现简单的请求速率限制
    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    
    // 默认的请求速率限制：每分钟60次请求
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    /**
     * 用户注册
     * <p>
     * 创建新用户账户，验证邮箱是否已存在，
     * 加密密码，保存用户信息，并生成JWT令牌。
     * </p>
     *
     * @param request 包含用户注册信息的请求DTO
     * @return 包含JWT令牌和用户信息的响应
     */
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(RegisterRequestDTO request) {
        logger.debug("Processing registration request for email: {}", request.getEmail());
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.info("Registration failed: Email already exists - {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(409, "Email already exists"));
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCountry(request.getCountry());
        user.setPhone(request.getPhone());
        userRepository.save(user);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成访问令牌和刷新令牌
            TokenPair tokenPair = generateTokenPairFromAuthentication(authentication);
            UserInfoDTO userInfo = new UserInfoDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
            
            // 记录安全审计日志
            logSecurityEvent("USER_REGISTERED", user.getEmail(), "User registered successfully");
            
            logger.info("User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<AuthResponseDTO>success(201, "User registered successfully",
                            new AuthResponseDTO(tokenPair.accessToken, tokenPair.refreshToken,
                                    tokenPair.accessTokenExpiresIn, userInfo)));
        } catch (Exception e) {
            logger.error("Error during authentication after registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error during authentication"));
        }
    }

    /**
     * 用户登录
     * <p>
     * 验证用户凭证，生成JWT令牌，并返回用户信息。
     * </p>
     *
     * @param request 包含登录凭证的请求DTO
     * @return 包含JWT令牌和用户信息的响应
     */
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(LoginRequestDTO request) {
        // 检查请求速率限制
        String ipAddress = "unknown"; // 在实际应用中，应该从请求中获取IP地址
        if (!checkRateLimit(ipAddress)) {
            logger.warn("Rate limit exceeded for IP: {}", ipAddress);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error(429, "Too many requests, please try again later"));
        }
        
        logger.debug("Processing login request");
        
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            logger.info("Login failed: User not found - {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Invalid credentials"));
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.info("Login failed: Invalid password for user - {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Invalid credentials"));
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成访问令牌和刷新令牌
            TokenPair tokenPair = generateTokenPairFromAuthentication(authentication);
            UserInfoDTO userInfo = new UserInfoDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
            
            // 记录安全审计日志
            logSecurityEvent("USER_LOGIN", user.getEmail(), "User logged in successfully");
            
            logger.info("User logged in successfully");
            return ResponseEntity.ok(ApiResponse.<AuthResponseDTO>success(200, "Login successful",
                    new AuthResponseDTO(tokenPair.accessToken, tokenPair.refreshToken,
                            tokenPair.accessTokenExpiresIn, userInfo)));
        } catch (BadCredentialsException e) {
            logger.info("Login failed: Bad credentials for user - {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Invalid credentials"));
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error during authentication"));
        }
    }

    /**
     * 验证认证令牌
     * <p>
     * 检查提供的认证头中的JWT令牌是否有效，
     * 验证令牌签名、过期时间，并检查令牌是否已被撤销。
     * </p>
     *
     * @param authHeader 包含Bearer令牌的认证头
     * @return 如果令牌有效则返回true，否则返回false
     */
    public boolean check(String authHeader) {
        logger.debug("Checking authentication token");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Invalid auth header format or null");
            return false;
        }
        
        String token = authHeader.substring(7);
        
        // 检查令牌是否已被撤销
        if (isTokenRevoked(token)) {
            logger.info("Token validation failed: Token has been revoked");
            return false;
        }
        
        try {
            // 验证令牌并提取用户名
            if (!jwtUtil.validateToken(token)) {
                logger.info("Token validation failed: Invalid token");
                return false;
            }
            
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                logger.info("Token validation failed: Username is null");
                return false;
            }
            
            // 检查用户是否存在
            boolean userExists = userRepository.findByEmail(username).isPresent();
            if (!userExists) {
                logger.info("Token validation failed: User not found - {}", username);
                return false;
            }
            
            logger.debug("Token validation successful for user: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 注销用户
     * <p>
     * 将用户的JWT令牌添加到撤销列表中，使其无效。
     * </p>
     *
     * @param authHeader 包含Bearer令牌的认证头
     * @return 包含注销状态的响应
     */
    public ResponseEntity<ApiResponse<Void>> logout(String authHeader) {
        logger.debug("Processing logout request");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("Logout failed: Invalid auth header");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "Invalid authentication header"));
        }
        
        String token = authHeader.substring(7);
        
        try {
            // 验证令牌
            if (jwtUtil.validateToken(token)) {
                // 将令牌添加到撤销列表
                revokeToken(token);
                
                // 清除安全上下文
                SecurityContextHolder.clearContext();
                
                // 提取用户名并记录安全审计日志
                String username = jwtUtil.extractUsername(token);
                logSecurityEvent("USER_LOGOUT", username, "User logged out successfully");
                
                logger.info("User logged out successfully");
                return ResponseEntity.ok(ApiResponse.<Void>success(200, "Logout successful", null));
            } else {
                logger.info("Logout failed: Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Invalid token"));
            }
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error during logout"));
        }
    }
    
    /**
     * 刷新访问令牌
     * <p>
     * 使用有效的刷新令牌生成新的访问令牌。
     * </p>
     *
     * @param refreshToken 刷新令牌
     * @return 包含新访问令牌的响应
     */
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(String refreshToken) {
        logger.debug("Processing token refresh request");
        
        try {
            // 验证刷新令牌
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                logger.info("Token refresh failed: Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Invalid refresh token"));
            }
            
            // 从刷新令牌中提取用户名
            String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
            if (username == null) {
                logger.info("Token refresh failed: Username is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Invalid refresh token"));
            }
            
            // 检查用户是否存在
            Optional<User> userOpt = userRepository.findByEmail(username);
            if (userOpt.isEmpty()) {
                logger.info("Token refresh failed: User not found - {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "User not found"));
            }
            
            User user = userOpt.get();
            
            // 生成新的访问令牌
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password("")
                    .authorities("USER")
                    .build();
            
            String newAccessToken = jwtUtil.generateToken(userDetails);
            UserInfoDTO userInfo = new UserInfoDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
            
            // 记录安全审计日志
            logSecurityEvent("TOKEN_REFRESH", username, "Access token refreshed successfully");
            
            logger.info("Access token refreshed successfully for user: {}", username);
            return ResponseEntity.ok(ApiResponse.<AuthResponseDTO>success(200, "Token refreshed successfully",
                    new AuthResponseDTO(newAccessToken, refreshToken,
                            jwtUtil.getAccessTokenExpirationMs(), userInfo)));
        } catch (Exception e) {
            logger.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error during token refresh"));
        }
    }
    
    /**
     * 从认证对象生成JWT令牌对
     *
     * @param authentication Spring Security认证对象
     * @return 包含访问令牌和刷新令牌的TokenPair对象
     */
    private TokenPair generateTokenPairFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return new TokenPair(accessToken, refreshToken, jwtUtil.getAccessTokenExpirationMs());
        } else {
            logger.warn("Principal is not an instance of UserDetails");
            return null;
        }
    }
    
    /**
     * 撤销令牌
     *
     * @param token 要撤销的JWT令牌
     */
    private void revokeToken(String token) {
        revokedTokens.add(token);
        logger.debug("Token revoked successfully");
    }
    
    /**
     * 检查令牌是否已被撤销
     *
     * @param token 要检查的JWT令牌
     * @return 如果令牌已被撤销则返回true，否则返回false
     */
    private boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
    
    /**
     * 检查请求速率限制
     *
     * @param key 请求标识符（通常是IP地址或用户ID）
     * @return 如果未超过速率限制则返回true，否则返回false
     */
    private boolean checkRateLimit(String key) {
        RequestCounter counter = requestCounters.computeIfAbsent(key, k -> new RequestCounter());
        return counter.incrementAndCheck();
    }
    
    /**
     * 记录安全审计日志
     *
     * @param eventType 事件类型
     * @param username 用户名
     * @param message 日志消息
     */
    private void logSecurityEvent(String eventType, String username, String message) {
        // 在实际应用中，应该将安全事件记录到专门的审计日志中
        // 这里简单地使用应用日志记录
        logger.info("SECURITY_AUDIT - Type: {}, User: {}, Message: {}, Time: {}",
                eventType, username, message, Instant.now());
    }
    
    /**
     * 令牌对，包含访问令牌和刷新令牌
     */
    private static class TokenPair {
        final String accessToken;
        final String refreshToken;
        final long accessTokenExpiresIn;
        
        TokenPair(String accessToken, String refreshToken, long accessTokenExpiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.accessTokenExpiresIn = accessTokenExpiresIn;
        }
    }
    
    /**
     * 请求计数器，用于实现简单的请求速率限制
     */
    private static class RequestCounter {
        private int count = 0;
        private long resetTime = System.currentTimeMillis() + 60000; // 1分钟后重置
        
        /**
         * 增加计数并检查是否超过限制
         *
         * @return 如果未超过速率限制则返回true，否则返回false
         */
        synchronized boolean incrementAndCheck() {
            long now = System.currentTimeMillis();
            if (now > resetTime) {
                count = 0;
                resetTime = now + 60000; // 重置为1分钟后
            }
            
            if (count < MAX_REQUESTS_PER_MINUTE) {
                count++;
                return true;
            }
            
            return false;
        }
    }
}
