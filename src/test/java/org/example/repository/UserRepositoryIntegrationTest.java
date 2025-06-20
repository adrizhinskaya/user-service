package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DisplayName("UserRepository Integration Tests")
public class UserRepositoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.6")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    private static EntityManagerFactory emf;
    private UserRepository userRepository;
    private EasyRandom generator;

    @BeforeAll
    static void setUpAll() {
        System.setProperty("tc.jdbc.url", postgres.getJdbcUrl());
        System.setProperty("tc.jdbc.username", postgres.getUsername());
        System.setProperty("tc.jdbc.password", postgres.getPassword());

        emf = Persistence.createEntityManagerFactory("user_service_test");
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        generator = new EasyRandom();
        userRepository = new UserRepository(emf);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("should add a user successfully and generate ID")
    void testAddUserPersistsSuccessfully() {
        // Given
        User user = generator.nextObject(User.class);
        user.setId(null);
        user.setEmail(user.getEmail() + System.nanoTime() + "@example.com");

        // When
        userRepository.add(user);

        // Then
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());

        EntityManager em = emf.createEntityManager();
        User foundUser = em.find(User.class, user.getId());
        em.close();

        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getAge(), foundUser.getAge());
        assertTrue(foundUser.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    @DisplayName("should throw exception when adding user with duplicate email")
    void testAddUserThrowsExceptionOnDuplicateEmail() {
        // Given
        String email = "test.duplicate@example.com";
        User user1 = generator.nextObject(User.class);
        user1.setId(null);
        user1.setEmail(email);
        userRepository.add(user1);

        User user2 = generator.nextObject(User.class);
        user2.setId(null);
        user2.setEmail(email);

        // When & Then
        assertThrows(RuntimeException.class, () -> userRepository.add(user2));

        EntityManager em = emf.createEntityManager();
        List<User> usersInDb = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        em.close();
        assertEquals(1, usersInDb.size());
        assertEquals(user1.getEmail(), usersInDb.get(0).getEmail());
    }

    @Test
    @DisplayName("should retrieve a user by ID")
    void testGetByIdFound() {
        // Given
        User user = generator.nextObject(User.class);
        user.setId(null);
        user.setEmail(user.getEmail() + System.nanoTime() + "@example.com");
        userRepository.add(user);

        // When
        User foundUser = userRepository.getById(user.getId());

        // Then
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
    }

    @Test
    @DisplayName("should return null when user not found by ID")
    void testGetByIdNotFound() {
        // Given
        Integer nonExistentId = 999;

        // When
        User foundUser = userRepository.getById(nonExistentId);

        // Then
        assertNull(foundUser);
    }

    @Test
    @DisplayName("should update an existing user")
    void testUpdateUserSuccessfully() {
        // Given
        User user = generator.nextObject(User.class);
        user.setId(null);
        user.setEmail(user.getEmail() + System.nanoTime() + "@example.com");
        userRepository.add(user);

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setName("Jane Doe Updated");
        updatedUser.setEmail("jane.doe.updated@example.com");
        updatedUser.setAge(35);
        updatedUser.setCreatedAt(user.getCreatedAt());

        // When
        userRepository.update(updatedUser);

        // Then
        EntityManager em = emf.createEntityManager();
        User fetchedUser = em.find(User.class, user.getId());
        em.close();

        assertNotNull(fetchedUser);
        assertEquals(updatedUser.getName(), fetchedUser.getName());
        assertEquals(updatedUser.getEmail(), fetchedUser.getEmail());
        assertEquals(updatedUser.getAge(), fetchedUser.getAge());
        assertEquals(user.getCreatedAt(), fetchedUser.getCreatedAt());
    }

    @Test
    @DisplayName("should throw exception when updating non-existent user")
    void testUpdateNonExistentUserThrowsException() {
        // Given
        User nonExistentUser = generator.nextObject(User.class);
        nonExistentUser.setId(999);

        // When & Then
        assertThrows(RuntimeException.class, () -> userRepository.update(nonExistentUser));

        EntityManager em = emf.createEntityManager();
        List<User> usersInDb = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        em.close();
        assertTrue(usersInDb.isEmpty());
    }

    @Test
    @DisplayName("should delete an existing user by ID")
    void testDeleteUserSuccessfully() {
        // Given
        User user = generator.nextObject(User.class);
        user.setId(null);
        user.setEmail(user.getEmail() + System.nanoTime() + "@example.com");
        userRepository.add(user);

        // When
        userRepository.delete(user.getId());

        // Then
        EntityManager em = emf.createEntityManager();
        User foundUser = em.find(User.class, user.getId());
        em.close();

        assertNull(foundUser);
    }

    @Test
    @DisplayName("should not throw error when deleting non-existent user")
    void testDeleteNonExistentUser() {
        // Given
        Integer nonExistentId = 999;

        // When & Then
        assertDoesNotThrow(() -> userRepository.delete(nonExistentId));

        EntityManager em = emf.createEntityManager();
        List<User> usersInDb = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        em.close();
        assertTrue(usersInDb.isEmpty());
    }
}
