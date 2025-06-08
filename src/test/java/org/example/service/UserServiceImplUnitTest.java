package org.example.service;

import org.example.exception.EmailConflictException;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.UserEntity;
import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private NewUserDto newUserDto;
    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        newUserDto = new NewUserDto("John Doe", "john.doe@example.com", 30);
        userEntity = new UserEntity(1L, "John Doe", "john.doe@example.com", 30, LocalDateTime.now());
        userDto = new UserDto(1L, "John Doe", "john.doe@example.com", 30, LocalDateTime.now());
    }

    @Test
    void create() {
        when(userMapper.toUserEntity(newUserDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);
        UserDto result = userService.create(newUserDto);
        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void create_emailConflict() {
        when(userRepository.findFirstByEmail(newUserDto.getEmail())).thenReturn(Optional.of(userEntity));
        assertThrows(EmailConflictException.class, () -> userService.create(newUserDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);
        UserDto result = userService.getById(1L);
        assertThat(result).isEqualTo(userDto);
    }

    @Test
    void getById_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);
        UserDto updatedUserDto = userService.update(1L, newUserDto);
        assertThat(updatedUserDto).isEqualTo(userDto);
        verify(userRepository, times(1)).save(userEntity);
    }


    @Test
    void update_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.update(1L, newUserDto));
    }

    @Test
    void update_emailConflict() {
        NewUserDto newUserDtoWithDiffEmail = new NewUserDto("Jane Doe", "jane.doe@example.com", 25);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(userRepository.findFirstByEmail(newUserDtoWithDiffEmail.getEmail())).thenReturn(Optional.of(new UserEntity(2L, "Jane Doe", "jane.doe@example.com", 25, LocalDateTime.now())));
        assertThrows(EmailConflictException.class, () -> userService.update(1L, newUserDtoWithDiffEmail));
    }

    @Test
    void delete() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
