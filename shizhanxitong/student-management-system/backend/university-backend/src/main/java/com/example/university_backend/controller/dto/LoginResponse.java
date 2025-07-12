package com.example.university_backend.controller.dto;

import com.example.university_backend.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private User user;
}