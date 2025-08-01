package com.example.flightapi.dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO {
    private Long id;
    private String flightNumber;
    private String departure;
    private String destination;
    private String departureDate;
    private String departureTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#,##0.00")
    private BigDecimal price;
}
