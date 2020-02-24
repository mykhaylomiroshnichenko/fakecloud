package com.fakecloud.dto.bridge;

import com.fakecloud.dto.model.UserDto;
import com.fakecloud.model.User;

public class UserToUserDtoBridge {
    public static UserDto build(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());

        return userDto;
    }
}
