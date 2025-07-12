package com.example.university_backend.controller;

import com.example.university_backend.entity.User;
import com.example.university_backend.service.UserService;
import com.example.university_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        System.out.println("收到登录请求: username=" + username + ", password=" + password);
        
        // 检查请求参数
        if (username == null || password == null) {
            System.out.println("用户名或密码为空");
            return ResponseEntity.badRequest().body(Map.of("message", "用户名和密码不能为空"));
        }
        
        try {
            // 查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                System.out.println("用户不存在: " + username);
                return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码错误"));
            }
            
            System.out.println("数据库中的用户: " + user);
            
            // 直接比较密码（不使用加密）
            if (!password.equals(user.getPassword())) {
                System.out.println("密码不匹配: 输入=" + password + ", 数据库=" + user.getPassword());
                return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码错误"));
            }
            
            // 生成JWT令牌 - 确保传递正确的类型
            String token = JwtUtil.generateToken(
                user.getId(), 
                user.getUsername(), 
                user.getRole() != null ? user.getRole() : "user"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            
            System.out.println("登录成功: " + username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("登录过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "登录失败: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User userRequest) {
        try {
            // 检查用户名是否已存在
            User existingUser = userService.findByUsername(userRequest.getUsername());
            if (existingUser != null) {
                return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
            }
            
            // 保存用户
            User savedUser = userService.save(userRequest);
            
            // 生成JWT令牌
            String token = JwtUtil.generateToken(
                savedUser.getId(), 
                savedUser.getUsername(), 
                savedUser.getRole() != null ? savedUser.getRole() : "user"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", savedUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("注册过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "注册失败: " + e.getMessage()));
        }
    }
}
