package com.fakecloud.dto.bridge;

import com.fakecloud.dto.request.RegistrationRequestDto;
import com.fakecloud.model.User;

public class RegistrationRequestDtoToUser {
    public static User build(RegistrationRequestDto registrationRequestDto) {
        User user = new User();
        user.setUsername(registrationRequestDto.getUsername());
        user.setFirstName(registrationRequestDto.getFirstName());
        user.setLastName(registrationRequestDto.getLastName());
        user.setPassword(registrationRequestDto.getPassword());

        return user;
    }
}
