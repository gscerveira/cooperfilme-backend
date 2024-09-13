package com.cooperfilme.roteiros.service;

import com.cooperfilme.roteiros.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findUserByEmail(String email);
    List<User> getAllUsers();
}