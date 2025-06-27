package com.example.flightapi.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
