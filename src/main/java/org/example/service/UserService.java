package org.example.service;

import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    UserDto getById(Long userId);

    UserDto update(Long userId, NewUserDto newUserDto);

    void delete(Long userId);
}
