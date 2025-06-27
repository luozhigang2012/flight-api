package com.example.flightapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerInfoDTO {
    @NotBlank(message = "{passenger.firstname.required}")
    private String firstName;
    
    @NotBlank(message = "{passenger.lastname.required}")
    private String lastName;
    
    @NotBlank(message = "{passenger.email.required}")
    @Email(message = "{passenger.email.invalid}")
    private String email;
}
