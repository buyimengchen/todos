package com.example.university_backend.service;

import com.example.university_backend.entity.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User save(User user);
    
    List<User> findAll();
    User findById(Integer id);
    void update(User user);
    void deleteById(Integer id);
    void updateStatus(Integer id, Integer status);
}
