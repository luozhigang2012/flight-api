package com.example.flightapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, message = "{password.min.length}")
    @Pattern(regexp = ".*[A-Za-z].*", message = "{password.require.letter}")
    @Pattern(regexp = ".*[0-9].*", message = "{password.require.number}")
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String country;

    private String phone;
}
