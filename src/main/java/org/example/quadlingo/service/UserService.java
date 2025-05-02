package org.example.quadlingo.service;

import org.example.quadlingo.User;
import org.example.quadlingo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private User currentUser = null;

    public UserService() {
    }

    public List<User> addUser(String name, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User(name, email, password, "USER");
        userRepository.save(user);
        return userRepository.findAll();
    }

    public User findUser(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            currentUser = user;
        }
        return user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateUser(String name, String email) {
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setEmail(email);
            userRepository.save(currentUser);
        }
    }

    public void deleteUser() {
        if (currentUser != null) {
            userRepository.delete(currentUser);
            logout(); // Выполняем выход после удаления
        }
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public boolean isUser() {
        return currentUser != null && "USER".equals(currentUser.getRole());
    }
}