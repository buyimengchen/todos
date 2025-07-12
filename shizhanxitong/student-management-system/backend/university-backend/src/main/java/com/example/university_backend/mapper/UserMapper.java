package com.example.university_backend.mapper;

import com.example.university_backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    void insert(User user);
    void update(User user);
    
    // 添加缺少的方法
    List<User> findAll();
    User findById(@Param("id") Integer id);
    void deleteById(@Param("id") Integer id);
    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
