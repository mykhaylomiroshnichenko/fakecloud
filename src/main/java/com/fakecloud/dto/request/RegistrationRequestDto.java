package com.fakecloud.dto.request;

import lombok.Data;

@Data
public class RegistrationRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
}
