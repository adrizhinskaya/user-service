package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;

public class UserService {
    private UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public void add(User user) {
        userRepository.add(user);
    }

    public void update(User newUser) {
        userRepository.update(newUser);
    }

    public User getById(Integer id) {
        return userRepository.getById(id);
    }

    public void deleteById(Integer id) {
        userRepository.delete(id);
    }
}