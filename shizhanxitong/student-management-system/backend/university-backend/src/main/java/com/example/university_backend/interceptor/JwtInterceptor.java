package com.example.university_backend.interceptor;

import com.example.university_backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未登录或token缺失");
            return false;
        }
        token = token.substring(7); // 去掉"Bearer "
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("token无效或已过期");
            return false;
        }
        Claims claims = JwtUtil.parseToken(token);
        request.setAttribute("userId", claims.get("userId"));
        request.setAttribute("username", claims.get("username"));
        request.setAttribute("role", claims.get("role"));
        return true;
    }
}