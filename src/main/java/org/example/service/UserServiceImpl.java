package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.EmailConflictException;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.UserEntity;
import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private ProducerService producerService;
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto create(NewUserDto newUserDto) {
        emailExistsCheck(null, newUserDto.getEmail());
        UserEntity entity = mapper.toUserEntity(newUserDto);
        entity.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        userRepository.save(entity);
        producerService.sendUserEvent("CREATE", entity.getEmail());
        return mapper.toUserDto(entity);
    }

    @Override
    public UserDto getById(Long userId) {
        return mapper.toUserDto(userExistsCheck(userId));
    }

    @Override
    public UserDto update(Long userId, NewUserDto newUserDto) {
        UserEntity entity = userExistsCheck(userId);
        if (newUserDto.getName() != null) {
            entity.setName(newUserDto.getName());
        }
        if (newUserDto.getAge() != null) {
            entity.setAge(newUserDto.getAge());
        }
        if (newUserDto.getEmail() != null) {
            emailExistsCheck(entity.getId(), newUserDto.getEmail());
            entity.setEmail(newUserDto.getEmail());
        }
        return mapper.toUserDto(userRepository.save(entity));
    }

    @Override
    public void delete(Long userId) {
        UserEntity entity = userExistsCheck(userId);
        userRepository.deleteById(userId);
        producerService.sendUserEvent("DELETE", entity.getEmail());
    }

    private UserEntity userExistsCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found ."));
    }

    private void emailExistsCheck(Long id, String email) {
        Optional<UserEntity> entity = userRepository.findFirstByEmail(email);
        if (entity.isPresent() && !Objects.equals(entity.get().getId(), id)) {
            throw new EmailConflictException("Such user email already exists .");
        }
    }
}