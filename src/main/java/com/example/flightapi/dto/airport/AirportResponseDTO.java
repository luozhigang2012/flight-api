package com.example.flightapi.dto.airport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirportResponseDTO {
    private Long id;
    private String code;
    private String name;
    private String city;
    private String country;
}
