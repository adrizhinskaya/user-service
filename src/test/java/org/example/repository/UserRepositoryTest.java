package org.example.repository;

import org.example.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should not find a user with email")
    void testFindFirstByEmailReturnEmpty() {
        UserEntity newUser = makeUserEntity();

        Optional<UserEntity> entity = userRepository.findFirstByEmail(newUser.getEmail());
        assertFalse(entity.isPresent());
    }

    @Test
    @DisplayName("Should find a user with email successfully")
    void testFindFirstByEmailSuccess() {
        UserEntity newUser = makeUserEntity();

        userRepository.save(newUser);
        Optional<UserEntity> entity = userRepository.findFirstByEmail(newUser.getEmail());

        assertTrue(entity.isPresent());
        assertEquals(1L, entity.get().getId());
        assertEquals(newUser.getName(), entity.get().getName());
        assertEquals(newUser.getEmail(), entity.get().getEmail());
        assertEquals(newUser.getAge(), entity.get().getAge());
        assertEquals(newUser.getCreatedAt(), entity.get().getCreatedAt());
    }

    private UserEntity makeUserEntity() {
        return UserEntity.builder()
                .name("John")
                .email("some@email.com")
                .age(22)
                .createdAt(LocalDateTime.now())
                .build();
    }
}