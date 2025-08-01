package com.example.flightapi.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * 工具类，用于处理区域设置相关的操作
 */
public class LocaleUtils {
    private static final Logger log = LoggerFactory.getLogger(LocaleUtils.class);
    
    // 默认区域设置
    public static final String DEFAULT_LOCALE = "en_US";
    
    // 语言参数名称
    public static final String LANG_PARAM = "lang";
    
    /**
     * 从请求中获取区域设置
     * @param request HTTP请求
     * @return 区域设置对象
     */
    public static Locale getLocaleFromRequest(HttpServletRequest request) {
        String lang = request.getParameter(LANG_PARAM);
        return parseLocale(lang);
    }
    
    /**
     * 解析语言字符串为区域设置对象
     * @param lang 语言字符串，例如 "en_US", "zh_CN", "en", "zh"
     * @return 区域设置对象
     */
    public static Locale parseLocale(String lang) {
        if (lang == null || lang.isEmpty()) {
            log.debug("No language specified, using default: {}", DEFAULT_LOCALE);
            return parseLocaleString(DEFAULT_LOCALE);
        }
        
        log.debug("Parsing locale from: {}", lang);
        return parseLocaleString(lang);
    }
    
    /**
     * 解析区域设置字符串
     * 支持以下格式：
     * - language_country (例如 "en_US")
     * - language (例如 "en")
     * @param localeStr 区域设置字符串
     * @return 区域设置对象
     */
    private static Locale parseLocaleString(String localeStr) {
        String[] parts = localeStr.split("_");
        if (parts.length > 1) {
            // 使用 Locale.of() 静态工厂方法替代已弃用的构造函数 (Java 19+)
            return Locale.of(parts[0], parts[1]);
        } else {
            // 使用 Locale.of() 静态工厂方法替代已弃用的构造函数 (Java 19+)
            return Locale.of(parts[0]);
        }
    }
}