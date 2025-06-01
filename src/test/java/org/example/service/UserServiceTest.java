package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private EasyRandom generator;

    @BeforeEach
    void setUp() {
        generator = new EasyRandom();
    }

    @Test
    @DisplayName("should add a user successfully")
    void testAddUserSuccess() {
        // Given
        User user = generator.nextObject(User.class);
        user.setId(null);

        // When
        userService.add(user);

        // Then
        verify(userRepository, times(1)).add(user);
    }

    @Test
    @DisplayName("should handle exception when adding user")
    void testAddUserWhenRepositoryThrowsException() {
        // Given
        User user = generator.nextObject(User.class);
        user.setId(null);
        RuntimeException expectedException = new RuntimeException("Database error during add");

        doThrow(expectedException).when(userRepository).add(any(User.class));

        // When & Then
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            userService.add(user);
        });

        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(userRepository, times(1)).add(user);
    }

    @Test
    @DisplayName("should update an existing user successfully")
    void testUpdateUserSuccess() {
        // Given
        User existingUser = generator.nextObject(User.class);
        existingUser.setId(1);
        User updatedUser = generator.nextObject(User.class);
        updatedUser.setId(existingUser.getId());
        updatedUser.setName("Updated Name");

        // When
        userService.update(updatedUser);

        // Then
        verify(userRepository, times(1)).update(updatedUser);
    }

    @Test
    @DisplayName("should handle exception when updating user")
    void testUpdateUserWhenRepositoryThrowsException() {
        // Given
        User userToUpdate = generator.nextObject(User.class);
        userToUpdate.setId(1);
        RuntimeException expectedException = new RuntimeException("Database error during update");
        doThrow(expectedException).when(userRepository).update(any(User.class));

        // When & Then
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            userService.update(userToUpdate);
        });

        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(userRepository, times(1)).update(userToUpdate);
    }

    @Test
    @DisplayName("should retrieve a user by ID successfully")
    void testGetByIdUserFound() {
        // Given
        Integer userId = 1;
        User foundUser = generator.nextObject(User.class);
        foundUser.setId(userId);
        foundUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.getById(userId)).thenReturn(foundUser);

        // When
        User resultUser = userService.getById(userId);

        // Then
        assertNotNull(resultUser);
        assertEquals(userId, resultUser.getId());
        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    @DisplayName("should return null when user not found by ID")
    void testGetByIdUserNotFound() {
        // Given
        Integer userId = 999;
        when(userRepository.getById(userId)).thenReturn(null);

        // When
        User resultUser = userService.getById(userId);

        // Then
        assertNull(resultUser);
        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    @DisplayName("should handle exception when getting user by ID")
    void testGetByIdWhenRepositoryThrowsException() {
        // Given
        Integer userId = 1;
        RuntimeException expectedException = new RuntimeException("Database error during getById");
        doThrow(expectedException).when(userRepository).getById(userId);

        // When & Then
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            userService.getById(userId);
        });

        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    @DisplayName("should delete a user by ID successfully")
    void testDeleteUserSuccess() {
        // Given
        Integer userIdToDelete = 1;

        // When
        userService.deleteById(userIdToDelete);

        // Then
        verify(userRepository, times(1)).delete(userIdToDelete);
    }

    @Test
    @DisplayName("should handle exception when deleting user by ID")
    void testDeleteUserWhenRepositoryThrowsException() {
        // Given
        Integer userIdToDelete = 1;
        RuntimeException expectedException = new RuntimeException("Database error during delete");
        doThrow(expectedException).when(userRepository).delete(userIdToDelete);

        // When & Then
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            userService.deleteById(userIdToDelete);
        });

        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(userRepository, times(1)).delete(userIdToDelete);
    }
}
