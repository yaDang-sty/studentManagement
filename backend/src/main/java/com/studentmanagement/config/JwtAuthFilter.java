package com.studentmanagement.config;

import com.studentmanagement.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // 鏀捐鐧诲綍鎺ュ彛
        if (path.equals("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 鏀捐 OPTIONS 棰勬璇锋眰
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 楠岃瘉 token
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(401);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"success\":false,\"message\":\"鏈彁渚泃oken鎴杢oken鏍煎紡閿欒\"}");
            return;
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.isValid(token)) {
            res.setStatus(401);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"success\":false,\"message\":\"token宸茶繃鏈熸垨鏃犳晥锛岃閲嶆柊鐧诲綍\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}

