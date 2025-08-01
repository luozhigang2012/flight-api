package com.example.flightapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SecurityException;  // 替换已弃用的 SignatureException
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类：负责JWT令牌的生成、验证和解析
 * 包含令牌创建、用户信息提取、有效性验证等功能
 */
@Service
@Slf4j
public class JwtUtil {
    /**
     * JWT密钥，从配置文件中读取，提供默认值
     * 注意：生产环境应使用更强的密钥
     */
    @Value("${jwt.secret:flightappsecretkeyflightappsecretkey1234}")
    private String jwtSecret;

    /**
     * 访问令牌过期时间（毫秒），默认为30分钟
     */
    @Value("${jwt.access-token.expiration:1800000}") // 30 minutes
    private long accessTokenExpirationMs;
    
    /**
     * 刷新令牌过期时间（毫秒），默认为7天
     */
    @Value("${jwt.refresh-token.expiration:604800000}") // 7 days
    private long refreshTokenExpirationMs;
    
    /**
     * 获取访问令牌过期时间（毫秒）
     * @return 访问令牌过期时间
     */
    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
    
    /**
     * 获取刷新令牌过期时间（毫秒）
     * @return 刷新令牌过期时间
     */
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
    
    /**
     * 获取用于签名JWT的密钥
     * @return 用于HMAC-SHA算法的密钥
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 从JWT令牌中提取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中提取指定的声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @return 解析后的声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 为指定用户生成JWT令牌
     * @param userDetails 用户详情
     * @return JWT令牌字符串
     */
    public String generateToken(UserDetails userDetails) {
        log.debug("Generating access token for user {}, expiration time: {} ms", userDetails.getUsername(), accessTokenExpirationMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return createToken(claims, userDetails.getUsername(), accessTokenExpirationMs);
    }
    
    /**
     * 为指定用户生成刷新令牌
     * @param userDetails 用户详情
     * @return JWT刷新令牌字符串
     */
    public String generateRefreshToken(UserDetails userDetails) {
        log.debug("Generating refresh token for user {}, expiration time: {} ms", userDetails.getUsername(), refreshTokenExpirationMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), refreshTokenExpirationMs);
    }

    /**
     * 创建JWT令牌
     * @param claims 自定义声明
     * @param subject 令牌主题（通常是用户名）
     * @return JWT令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌
     * @param userDetails 用户详情
     * @return 如果令牌有效则返回true
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            
            if (!isValid) {
                log.debug("Token validation failed, user: {}, token expired: {}",
                        userDetails.getUsername(), isTokenExpired(token));
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查令牌是否已过期
     * @param token JWT令牌
     * @return 如果令牌已过期则返回true
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 从令牌中提取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * 验证JWT令牌的有效性（不需要UserDetails）
     * <p>
     * 此方法仅验证令牌的格式、签名和过期时间，
     * 不验证令牌中的用户名是否匹配特定用户。
     * 适用于只需要验证令牌本身有效性的场景。
     * </p>
     *
     * @param token JWT令牌
     * @return 如果令牌格式正确、签名有效且未过期则返回true
     */
    public boolean validateToken(String token) {
        try {
            // 尝试解析令牌，这会验证签名
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            
            // 检查令牌是否过期
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.info("JWT token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {  // 使用 SecurityException 替代已弃用的 SignatureException
            log.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证刷新令牌的有效性
     *
     * @param token 刷新令牌
     * @return 如果令牌有效则返回true
     */
    public boolean validateRefreshToken(String token) {
        try {
            // 验证令牌签名和格式
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            // 验证令牌类型
            String tokenType = (String) claims.get("type");
            if (!"refresh".equals(tokenType)) {
                log.warn("Token type is not refresh token: {}", tokenType);
                return false;
            }
            
            // 检查令牌是否过期
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.info("Refresh token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported refresh token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Malformed refresh token: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {  // 使用 SecurityException 替代已弃用的 SignatureException
            log.error("Invalid refresh token signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating refresh token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从刷新令牌中提取用户名
     *
     * @param refreshToken 刷新令牌
     * @return 用户名，如果令牌无效则返回null
     */
    public String getUsernameFromRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
            
            // 验证令牌类型
            String tokenType = (String) claims.get("type");
            if (!"refresh".equals(tokenType)) {
                log.warn("Token type is not refresh token: {}", tokenType);
                return null;
            }
            
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from refresh token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中提取所有声明
     * @param token JWT令牌
     * @return 声明对象
     * @throws ExpiredJwtException 如果令牌已过期
     * @throws UnsupportedJwtException 如果令牌格式不支持
     * @throws MalformedJwtException 如果令牌格式错误
     * @throws SecurityException 如果令牌签名验证失败
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
