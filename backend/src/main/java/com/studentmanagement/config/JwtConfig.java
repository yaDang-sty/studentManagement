package com.studentmanagement.config;

import com.studentmanagement.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {

    @Value("${jwt.expiration:7200000}")
    private long expirationMs;

    @PostConstruct
    public void init() {
        JwtUtil.setExpiration(expirationMs);
        System.out.println("JWT 杩囨湡鏃堕棿宸茶缃? " + expirationMs + "ms (" + (expirationMs / 3600000) + "灏忔椂)");
    }
}

