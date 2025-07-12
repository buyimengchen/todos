package com.example.university_backend.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}