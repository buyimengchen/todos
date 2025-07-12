package com.example.university_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)  // 禁用Spring Security的CORS处理
            .csrf(AbstractHttpConfigurer::disable)  // 禁用CSRF保护
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()  // 允许所有请求
            );
        
        return http.build();
    }
}


