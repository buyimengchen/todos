package com.example.university_backend.controller;

import com.example.university_backend.entity.User;
import com.example.university_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll(HttpServletRequest request) {
        // 获取当前用户的角色和ID
        String role = (String) request.getAttribute("role");
        Integer userId = (Integer) request.getAttribute("userId");

        System.out.println("获取用户列表 - 当前用户角色: " + role + ", 用户ID: " + userId);

        // 只有管理员可以获取所有用户列表
        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok(userService.findAll());
        } else {
            // 非管理员只能获取自己的信息
            User currentUser = userService.findById(userId);
            if (currentUser != null) {
                List<User> userList = new ArrayList<>();
                userList.add(currentUser);
                return ResponseEntity.ok(userList);
            } else {
                return ResponseEntity.ok(new ArrayList<>());
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Integer id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");

        System.out.println("获取用户详情 - 当前用户角色: " + role + ", 当前用户ID: " + currentUserId + ", 请求用户ID: " + id);

        // 管理员可以查看任何用户，其他用户只能查看自己
        if (!"ADMIN".equals(role) && !currentUserId.equals(id)) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody User user, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        System.out.println("新增用户 - 当前用户角色: " + role);

        // 只有管理员可以新增用户
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足，只有管理员可以新增用户"));
        }

        return ResponseEntity.ok(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody User user, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");

        System.out.println("更新用户 - 当前用户角色: " + role + ", 当前用户ID: " + currentUserId + ", 更新用户ID: " + id);

        // 管理员可以更新任何用户，其他用户只能更新自己
        if (!"ADMIN".equals(role) && !currentUserId.equals(id)) {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足，只能修改自己的信息"));
        }

        user.setId(id);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        System.out.println("删除用户 - 当前用户角色: " + role + ", 删除用户ID: " + id);

        // 只有管理员可以删除用户
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足，只有管理员可以删除用户"));
        }

        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> statusMap, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        System.out.println("更新用户状态 - 当前用户角色: " + role + ", 更新用户ID: " + id);

        // 只有管理员可以更新用户状态
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足，只有管理员可以更新用户状态"));
        }

        Integer status = statusMap.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "状态不能为空"));
        }
        userService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}

