package com.example.flightapi.controller;

import com.example.flightapi.dto.ApiResponse;
import com.example.flightapi.dto.PagedResponseDTO;
import com.example.flightapi.dto.booking.BookingRequestDTO;
import com.example.flightapi.dto.booking.BookingResponseDTO;
import com.example.flightapi.entity.User;
import com.example.flightapi.exception.ResourceNotFoundException;
import com.example.flightapi.exception.UnauthorizedException;
import com.example.flightapi.service.BookingService;
import com.example.flightapi.repository.UserRepository;
import com.example.flightapi.util.ApplicationContextProvider;
import com.example.flightapi.util.LocaleUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository;

    /**
     * 获取当前登录用户
     * @return 当前登录用户，如果未登录则抛出UnauthorizedException
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UnauthorizedException("user.unauthorized");
        }
        
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("user.unauthorized"));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestBody @Valid BookingRequestDTO bookingRequest,
            HttpServletRequest request) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Creating booking for user: {} with language: {}", currentUser.getEmail(), lang);
        
        // 订票接口（POST /api/bookings）直接返回成功响应，Mock付款流程。
        BookingResponseDTO saved = bookingService.createBooking(bookingRequest);
        
        // 使用国际化消息
        String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage("booking.created", null, locale);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, successMessage, saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponseDTO<BookingResponseDTO>>> getBookings(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Fetching bookings for user: {} with status: {}, page: {}, size: {}, language: {}",
                currentUser.getEmail(), status, page, size, lang);
        
        // Spring Data JPA的页码从0开始，而客户端通常从1开始
        Pageable pageable = PageRequest.of(page - 1, size);
        PagedResponseDTO<BookingResponseDTO> bookings;
        if (status != null) {
            bookings = bookingService.getBookingsByUserAndStatus(currentUser, status, pageable);
        } else {
            bookings = bookingService.getBookingsByUser(currentUser, pageable);
        }
        
        // 检查结果是否为空
        if (bookings.getContent().isEmpty()) {
            if (status != null) {
                throw new ResourceNotFoundException("booking.status.empty", new Object[]{status});
            } else {
                throw new ResourceNotFoundException("booking.list.empty", null);
            }
        }
        
        // 使用国际化消息
        String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage("booking.list.success", null, locale);
        
        return ResponseEntity.ok(ApiResponse.success(200, successMessage, bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        
        // 获取语言参数
        String lang = request.getParameter(LocaleUtils.LANG_PARAM);
        Locale locale = LocaleUtils.parseLocale(lang);
        
        log.info("Getting booking detail with ID: {} for user: {} and language: {}",
                id, currentUser.getEmail(), lang);
        
        // 获取预订详情
        Optional<BookingResponseDTO> bookingOpt = bookingService.getBookingById(id);
        
        // 检查预订是否存在
        if (bookingOpt.isEmpty()) {
            throw new ResourceNotFoundException("booking.not.found", new Object[]{id});
        }
        
        BookingResponseDTO booking = bookingOpt.get();
        
        // 检查用户是否有权限查看此预订（只能查看自己的预订）
        if (!booking.getUserId().equals(currentUser.getId())) {
            log.warn("User {} attempted to access booking {} belonging to user {}",
                    currentUser.getEmail(), id, booking.getUserId());
            throw new UnauthorizedException("user.unauthorized");
        }
        
        // 使用国际化消息
        String successMessage = ApplicationContextProvider.getBean(MessageSource.class)
                .getMessage("booking.detail.success", null, locale);
        
        return ResponseEntity.ok(ApiResponse.success(200, successMessage, booking));
    }
}
