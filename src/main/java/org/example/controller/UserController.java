package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logger.ColoredCRUDLogger;
import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated @RequestBody NewUserDto newUserDto) {
        ColoredCRUDLogger.logPost("/users", newUserDto.toString());
        UserDto result = userService.create(newUserDto);
        ColoredCRUDLogger.logPostComplete("/users", result.toString());
        return result;
    }

    @GetMapping("/user/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        String url = String.format("/users/{%s}", userId);
        ColoredCRUDLogger.logGet(url);
        UserDto result = userService.getById(userId);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/user/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @Validated @RequestBody NewUserDto newUserDto) {
        String url = String.format("/users/{%s}", userId);
        ColoredCRUDLogger.logPatch(url, newUserDto.toString());
        UserDto result = userService.update(userId, newUserDto);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        String url = String.format("/users/{%s}", userId);
        ColoredCRUDLogger.logDelete(url);
        userService.delete(userId);
        ColoredCRUDLogger.logDeleteComplete(url);
    }
}