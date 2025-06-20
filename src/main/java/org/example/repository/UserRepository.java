package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class UserRepository {
    private final EntityManagerFactory emf;

    public UserRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("user_service");
    }

    public void add(User user) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            user.setId(null);
            user.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            transaction.begin();
            em.persist(user);
            transaction.commit();
            log.info("User added successfully: {}", user);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back during add operation for user: {}", user);
            }
            log.error("Error adding user: {}", user, e);
            throw new RuntimeException("Failed to add user", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public User getById(Integer id) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            User user = em.find(User.class, id);
            if (user != null) {
                log.info("User found with id {}: {}", id, user);
            } else {
                log.info("No user found with id {}", id);
            }
            return user;
        } catch (Exception e) {
            log.error("Error retrieving user with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user by ID", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void update(User newUser) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            User user = em.find(User.class, newUser.getId());
            if (user == null) {
                log.warn("Attempted to update non-existing user with id {}", newUser.getId());
                transaction.rollback();
                throw new RuntimeException("User not found for update with ID: " + newUser.getId());
            }
            user.setName(newUser.getName());
            user.setEmail(newUser.getEmail());
            user.setAge(newUser.getAge());
            em.merge(user);
            transaction.commit();
            log.info("User updated successfully: {}", user);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back during update operation for user: {}", newUser.getId());
            }
            log.error("Error updating user {}: {}", newUser.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update user", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void delete(Integer id) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                log.info("User deleted successfully with id {}", id);
            } else {
                log.info("No user found to delete with id {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back during delete operation for user: {}", id);
            }
            log.error("Error deleting user with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
