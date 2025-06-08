package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.EmailConflictException;
import org.example.exception.EntityNotFoundException;
import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserServiceImplTest {
    private final UserService userService;
    private final UserRepository userRepository;
    private NewUserDto newUserDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        newUserDto = makeNewUserDto();
        userDto = makeUserDto();
    }

    @Test
    @DisplayName("Should add a user successfully")
    void TestCreateUserSuccess() {
        UserDto createdUser = userService.create(newUserDto);
        assertThat(createdUser.getId()).isNotNull();
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        assertEquals(userDto.getAge(), createdUser.getAge());
        assertThat(createdUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle exception when creating user")
    void testCreateUserWhenEmailConflict() {
        UserDto createdUser = userService.create(newUserDto);
        assertThrows(EmailConflictException.class, () -> userService.create(newUserDto));
    }

    @Test
    @DisplayName("Should get a user by id successfully")
    void testGetUserByIdSuccess() {
        UserDto createdUser = userService.create(newUserDto);
        UserDto retrievedUser = userService.getById(createdUser.getId());
        assertEquals(retrievedUser.getId(), createdUser.getId());
        assertEquals(retrievedUser.getName(), createdUser.getName());
        assertEquals(retrievedUser.getEmail(), createdUser.getEmail());
        assertEquals(retrievedUser.getAge(), createdUser.getAge());
        assertEquals(retrievedUser.getCreatedAt(), createdUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle exception when getting user")
    void testGetByIdUserWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    @DisplayName("Should update a user successfully")
    void testUpdateUserSuccess_OnlyName() {
        UserDto createdUser = userService.create(newUserDto);
        NewUserDto newUser = new NewUserDto("Updated Name", null, null);
        UserDto updatedUser = userService.update(createdUser.getId(), newUser);

        assertEquals(updatedUser.getName(), newUser.getName());
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getAge(), createdUser.getAge());
        assertEquals(updatedUser.getCreatedAt(), createdUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should update a user successfully")
    void testUpdateUserSuccess_onlyEmail() {
        UserDto createdUser = userService.create(newUserDto);
        NewUserDto newUser = new NewUserDto(null, "test@example.com", null);
        UserDto updatedUser = userService.update(createdUser.getId(), newUser);

        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), newUser.getEmail());
        assertEquals(updatedUser.getAge(), createdUser.getAge());
        assertEquals(updatedUser.getCreatedAt(), createdUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should update a user successfully")
    void testUpdateUserSuccess_onlyAge() {
        UserDto createdUser = userService.create(newUserDto);
        NewUserDto newUser = new NewUserDto(null, null, 25);
        UserDto updatedUser = userService.update(createdUser.getId(), newUser);

        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getAge(), newUser.getAge());
        assertEquals(updatedUser.getCreatedAt(), createdUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should update a user successfully")
    void testUpdateUserSuccess_allFields() {
        UserDto createdUser = userService.create(newUserDto);
        NewUserDto newUser = new NewUserDto("Updated Name", "test@example.com", 25);
        UserDto updatedUser = userService.update(createdUser.getId(), newUser);

        assertEquals(updatedUser.getName(), newUser.getName());
        assertEquals(updatedUser.getEmail(), newUser.getEmail());
        assertEquals(updatedUser.getAge(), newUser.getAge());
        assertEquals(updatedUser.getCreatedAt(), createdUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException when updating user")
    void testUpdateUserWhenUserNotFound() {
        assertThrows(RuntimeException.class, () -> userService.update(999L, newUserDto));
    }

    @ParameterizedTest
    @MethodSource("provideValidEmails")
    @DisplayName("Should update a user successfully by unique emails")
    void testUpdateUserSuccess_validEmails(String email) {
        userService.create(newUserDto);
        UserDto user2 = userService.create(new NewUserDto("Test User 2", "some2@email.com", 30));

        NewUserDto updateDto = new NewUserDto("Updated Name", email, 35);
        UserDto updatedUser = userService.update(user2.getId(), updateDto);

        assertEquals(updatedUser.getEmail(), updateDto.getEmail());
    }

    static Stream<Arguments> provideValidEmails() {
        return Stream.of(
                Arguments.of("some2@email.com", true),
                Arguments.of("some3@email.com", true)
        );
    }

    @Test
    @DisplayName("Should handle EmailConflictException when updating user")
    void testUpdateUserWhenUserEmailConflict() {
        userService.create(newUserDto);
        UserDto user2 = userService.create(new NewUserDto("Test User 2", "some2@email.com", 30));

        NewUserDto updateDto = new NewUserDto("Updated Name", "some@email.com", 35);
        assertThrows(EmailConflictException.class, () -> userService.update(user2.getId(), updateDto));
    }

    @Test
    @DisplayName("Should delete a user successfully")
    void testDeleteUserSuccess() {
        long userId = userService.create(newUserDto).getId();
        userService.delete(userId);
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    private NewUserDto makeNewUserDto() {
        return NewUserDto.builder()
                .name("John")
                .email("some@email.com")
                .age(22)
                .build();
    }

    private UserDto makeUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("John")
                .email("some@email.com")
                .age(22)
                .build();
    }
}