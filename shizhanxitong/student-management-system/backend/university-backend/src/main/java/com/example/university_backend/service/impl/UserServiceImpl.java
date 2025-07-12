package com.example.university_backend.service.impl;

import com.example.university_backend.entity.User;
import com.example.university_backend.mapper.UserMapper;
import com.example.university_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.update(user);
        }
        return user;
    }
    
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }
    
    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }
    
    @Override
    public void update(User user) {
        // 确保status字段是整数类型
        if (user.getStatus() != null) {
            user.setStatus(user.getStatus());
        }
        
        // 如果密码为空，不更新密码
        if (user.getPassword() != null && user.getPassword().trim().isEmpty()) {
            User existingUser = userMapper.findById(user.getId());
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }
        
        userMapper.update(user);
    }
    
    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }
    
    @Override
    public void updateStatus(Integer id, Integer status) {
        userMapper.updateStatus(id, status);
    }
}
