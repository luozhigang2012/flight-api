package com.example.flightapi.controller;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.dto.PagedResponseDTO;
import com.example.flightapi.dto.flight.FlightResponseDTO;
import com.example.flightapi.exception.ResourceNotFoundException;
import com.example.flightapi.service.FlightService;
import com.example.flightapi.util.ApplicationContextProvider;
import com.example.flightapi.util.LocaleUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Slf4j
public class FlightController {
    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponseDTO<FlightResponseDTO>>> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Searching flights from: {} to: {} on date: {}, page: {}, size: {}, language: {}",
                from, to, date, page, size, lang);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        PagedResponseDTO<FlightResponseDTO> flights = flightService.searchFlights(from, to, date, pageable);
        
        // 检查结果是否为空
        if (flights.getContent().isEmpty()) {
            throw new ResourceNotFoundException("flight.search.empty", new Object[]{from, to, date});
        }
        
        // 使用国际化消息
        String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage("flight.search.success", null, locale);
        
        return ResponseEntity.ok(ApiResponse.success(200, successMessage, flights));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightResponseDTO>> getFlightDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Getting flight detail with ID: {}, language: {}", id, lang);
        
        Optional<FlightResponseDTO> flightOpt = flightService.getFlightById(id);
        
        if (flightOpt.isEmpty()) {
            throw new ResourceNotFoundException("flight.not.found", new Object[]{id});
        }
        
        // 使用国际化消息
        String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage("flight.detail.success", null, locale);
        
        return ResponseEntity.ok(ApiResponse.success(200, successMessage, flightOpt.get()));
    }
}
