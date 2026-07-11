package com.studentmanagement.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "MySuperSecretKeyForJWT2026StudentManagementSystem!@#$";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static long EXPIRATION = 24 * 60 * 60 * 1000; // 榛樿24灏忔椂

    public static void setExpiration(long ms) {
        EXPIRATION = ms;
    }

    public static String generateToken(String studentNo, String name) {
        return Jwts.builder()
                .subject(studentNo)
                .claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    public static String getStudentNo(String token) {
        return parseClaims(token).getSubject();
    }

    public static boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

