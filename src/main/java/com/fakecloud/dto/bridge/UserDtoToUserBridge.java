package com.fakecloud.dto.bridge;

import com.fakecloud.dto.model.UserDto;
import com.fakecloud.model.User;

public class UserDtoToUserBridge {
    public static User build(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return user;
    }
}
