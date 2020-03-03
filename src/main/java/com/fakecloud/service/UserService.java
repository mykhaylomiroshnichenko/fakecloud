package com.fakecloud.service;

import com.fakecloud.exception.BadRequestException;
import com.fakecloud.model.User;

import java.util.List;

public interface UserService {
    User register(User user) throws BadRequestException;

    List<User> getAll();

    User findByUsername(String username);

    User findById(Long id);

    void delete(Long id);
}
