package com.example.university_backend;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        System.out.println("本地生成的加密串: " + hash);
        System.out.println("本地校验: " + encoder.matches("123456", hash));
    }
}