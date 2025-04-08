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
        User user = new User(name, email, password);
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

    public void logout() {
        currentUser = null;
    }
}