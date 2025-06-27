package com.example.flightapi.dto.booking;

import com.example.flightapi.dto.PassengerInfoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    @NotNull(message = "{flight.id.required}")
    private Long flightId;
    
    @NotNull(message = "{user.id.required}")
    private Long userId;
    
    @NotEmpty(message = "{passengers.not.empty}")
    @Valid
    private List<PassengerInfoDTO> passengers;
}
