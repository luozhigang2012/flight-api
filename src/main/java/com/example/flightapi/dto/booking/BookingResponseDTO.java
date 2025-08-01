package com.example.flightapi.dto.booking;

import com.example.flightapi.dto.PassengerInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private String reference;
    private String status;
    private Long userId;
    private Long flightId;
    private LocalDateTime bookingTime;
    private BigDecimal totalPrice;
    private List<PassengerInfoDTO> passengers;
}
