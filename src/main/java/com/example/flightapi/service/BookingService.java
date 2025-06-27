package com.example.flightapi.service;

import com.example.flightapi.dto.PagedResponseDTO;
import com.example.flightapi.dto.booking.BookingRequestDTO;
import com.example.flightapi.dto.booking.BookingResponseDTO;
import com.example.flightapi.entity.Booking;
import com.example.flightapi.entity.BookingStatus;
import com.example.flightapi.entity.Flight;
import com.example.flightapi.entity.Passenger;
import com.example.flightapi.entity.User;
import com.example.flightapi.exception.BusinessRuleException;
import com.example.flightapi.exception.ResourceNotFoundException;
import com.example.flightapi.mapper.BookingMapper;
import com.example.flightapi.mapper.PassengerMapper;
import com.example.flightapi.repository.BookingRepository;
import com.example.flightapi.repository.FlightRepository;
import com.example.flightapi.repository.PassengerRepository;
import com.example.flightapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final BookingMapper bookingMapper;
    private final PassengerMapper passengerMapper;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequest) {
        log.info("Creating booking for flight ID: {} and user ID: {}", bookingRequest.getFlightId(), bookingRequest.getUserId());
        
        // 获取用户信息
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found", new Object[]{bookingRequest.getUserId()}));
        
        // 获取航班信息
        Flight flight = flightRepository.findById(bookingRequest.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("flight.not.found", new Object[]{bookingRequest.getFlightId()}));
        
        // 检查航班是否有足够的座位
        if (bookingRequest.getPassengers().size() > getAvailableSeats(flight)) {
            throw new BusinessRuleException("flight.not.enough.seats", new Object[]{flight.getFlightNumber()});
        }
        
        // 创建预订
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setReference(generateBookingReference());
        booking.setStatus(BookingStatus.UPCOMING);
        booking.setBookingTime(LocalDateTime.now());
        
        // 计算总价格
        BigDecimal totalPrice = flight.getPrice().multiply(new BigDecimal(bookingRequest.getPassengers().size()));
        booking.setTotalPrice(totalPrice);
        
        // 保存预订
        Booking savedBooking = bookingRepository.save(booking);
        
        // 处理乘客信息
        List<Passenger> passengers = new ArrayList<>();
        for (var passengerInfo : bookingRequest.getPassengers()) {
            Passenger passenger = passengerMapper.toEntity(passengerInfo);
            passenger.setBooking(savedBooking);
            passengers.add(passenger);
        }
        
        // 保存乘客信息
        List<Passenger> savedPassengers = passengerRepository.saveAll(passengers);
        savedBooking.setPassengers(savedPassengers);
        
        log.info("Booking created successfully with reference: {}", savedBooking.getReference());
        return bookingMapper.toDto(savedBooking);
    }

    public PagedResponseDTO<BookingResponseDTO> getBookingsByUser(User user, Pageable pageable) {
        log.info("Fetching bookings for user ID: {}", user.getId());
        return bookingMapper.toPagedResponseDto(bookingRepository.findByUser(user, pageable));
    }

    public PagedResponseDTO<BookingResponseDTO> getBookingsByUserAndStatus(User user, String status, Pageable pageable) {
        log.info("Fetching bookings for user ID: {} with status: {}", user.getId(), status);
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status);
            log.info("Converted status to enum: {}", bookingStatus);
            return bookingMapper.toPagedResponseDto(bookingRepository.findByUserAndStatus(user, bookingStatus, pageable));
        } catch (IllegalArgumentException e) {
            log.error("Invalid booking status: {}. Valid values are: {}", status,
                      java.util.Arrays.toString(BookingStatus.values()));
            throw new IllegalArgumentException("Invalid booking status: " + status +
                      ". Valid values are: " + java.util.Arrays.toString(BookingStatus.values()), e);
        }
    }

    public Optional<BookingResponseDTO> getBookingById(Long id) {
        log.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id).map(bookingMapper::toDto);
    }
    
    public Optional<BookingResponseDTO> getBookingByReference(String reference) {
        log.info("Fetching booking with reference: {}", reference);
        return bookingRepository.findByReference(reference).map(bookingMapper::toDto);
    }
    
    // 生成唯一的预订引用号
    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // 获取航班可用座位数（简化实现，实际应考虑已预订座位）
    private int getAvailableSeats(Flight flight) {
        // 这里应该有更复杂的逻辑来计算可用座位
        // 简化实现，假设每个航班有100个座位
        return 100;
    }
}
