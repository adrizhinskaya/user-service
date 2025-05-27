package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

@Slf4j
public class UserRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("user_service");

    public void add(User user) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            user.setCreatedAt(LocalDateTime.now());
            transaction.begin();
            em.persist(user);
            transaction.commit();
            log.info("User added successfully: {}", user);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back during add operation for user: {}", user);
            }
            log.error("Error adding user: {}", user, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public User getById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, id);
            if (user != null) {
                log.info("User found with id {}: {}", id, user);
            } else {
                log.info("No user found with id {}", id);
            }
            return user;
        } catch (Exception e) {
            log.error("Error retrieving user with id {}", id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(User newUser) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            User user = em.find(User.class, newUser.getId());
            if (user == null) {
                log.warn("Attempted to update non-existing user with id {}", newUser.getId());
                throw new RuntimeException("User not found .");
            }
            user.setName(newUser.getName());
            user.setEmail(newUser.getEmail());
            user.setAge(newUser.getAge());
            em.merge(user);
            transaction.commit();
            log.info("User updated successfully: {}", user);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back during update operation for user: {}", newUser);
            }
            log.error("Error updating user: {}", newUser, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
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
            if (transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back during delete operation for user id {}", id);
            }
            log.error("Error deleting user with id {}", id, e);
            throw e;
        } finally {
            em.close();
        }
    }
}