package com.example.flightapi.controller;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.dto.airport.AirportResponseDTO;
import com.example.flightapi.service.AirportService;
import com.example.flightapi.util.ApplicationContextProvider;
import com.example.flightapi.util.LocaleUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
@Slf4j
public class AirportController {
    private final AirportService airportService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AirportResponseDTO>>> getAirports(HttpServletRequest request) {
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Fetching all airports, language: {}", lang);
        
        List<AirportResponseDTO> airports = airportService.getAllAirports();
        
        // 使用国际化消息
        String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage("airport.list.success", null, locale);
        
        return ResponseEntity.ok(ApiResponse.success(200, successMessage, airports));
    }
}
