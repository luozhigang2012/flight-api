package com.example.flightapi.config;

import com.example.flightapi.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security配置类
 * <p>
 * 配置安全过滤器链、密码编码器和认证管理器。
 * 定义了API端点的访问权限规则和JWT认证过滤器。
 * </p>
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * <p>
     * 定义了以下安全配置：
     * 1. 禁用CSRF保护（因为使用JWT作为唯一的安全凭证）
     * 2. 使用无状态会话管理（JWT是自包含的，不需要服务器端会话）
     * 3. 配置公开API端点和受保护API端点
     * 4. 添加JWT认证过滤器
     * </p>
     *
     * @param http HttpSecurity对象
     * @return 配置好的SecurityFilterChain
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 添加日志记录安全配置
        log.info("Configure the security filter chain to allow access to the endpoint: /api/auth/**, /api/airports/**, /api/flights/**, /swagger-ui/**, /v3/api-docs/**, /actuator/**");
        
        http
            // 禁用CSRF保护，因为我们使用JWT作为唯一的安全凭证
            .csrf(csrf -> csrf.disable())
            // 配置CORS，允许来自特定源的跨域请求
            // 使用CorsConfigurationSource来定义CORS配置
            // 允许来自http://localhost:3000的请求
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置无状态会话管理，因为JWT是自包含的
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 公开的API端点，不需要认证
                .requestMatchers(
                    "/api/auth/**",
                    "/api/airports/**",
                    "/api/flights/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/actuator/**"
                ).permitAll()
                // 所有其他请求需要认证
                .anyRequest().authenticated()
            )
            // 在UsernamePasswordAuthenticationFilter之前添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * 配置密码编码器
     * <p>
     * 使用BCrypt算法进行密码哈希，这是一种强哈希算法，
     * 自动包含盐值并且计算复杂度可调整。
     * </p>
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     * <p>
     * 认证管理器负责处理认证请求，验证用户凭证。
     * </p>
     *
     * @param config 认证配置
     * @return AuthenticationManager实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置CORS
     * <p>
     * 定义了以下CORS配置：
     * 1. 允许来自"http://localhost:3000"的请求
     * 2. 允许所有HTTP方法（GET, POST, PUT, DELETE等）
     * 3. 允许所有请求头
     * </p>
     *
     * @return CorsConfigurationSource实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
