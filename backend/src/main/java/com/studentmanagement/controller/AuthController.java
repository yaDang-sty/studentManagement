package com.studentmanagement.controller;

import com.studentmanagement.entity.LoginRequest;
import com.studentmanagement.entity.LoginResponse;
import com.studentmanagement.entity.StudentInfo;
import com.studentmanagement.repository.StudentRepository;
import com.studentmanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.ok(new LoginResponse(false, "瀵嗙爜涓嶈兘涓虹┖"));
        }

        String loginType = request.getLoginType() != null ? request.getLoginType() : "studentNo";

        if ("phone".equals(loginType)) {
            String phone = request.getPhone();
            if (phone == null || phone.isEmpty()) {
                return ResponseEntity.ok(new LoginResponse(false, "鎵嬫満鍙蜂笉鑳戒负绌?));
            }
            return studentRepository.findByPhoneAndPassword(phone, request.getPassword())
                    .map(student -> {
                        LoginResponse resp = new LoginResponse(true, "鐧诲綍鎴愬姛");
                        resp.setToken(JwtUtil.generateToken(student.getStudentNo(), student.getName()));
                        resp.setStudent(StudentInfo.fromStudent(student));
                        return ResponseEntity.ok(resp);
                    })
                    .orElse(ResponseEntity.ok(new LoginResponse(false, "鎵嬫満鍙锋垨瀵嗙爜閿欒")));
        } else {
            String studentNo = request.getStudentNo();
            if (studentNo == null || studentNo.isEmpty()) {
                return ResponseEntity.ok(new LoginResponse(false, "瀛﹀彿涓嶈兘涓虹┖"));
            }
            return studentRepository.findByStudentNoAndPassword(studentNo, request.getPassword())
                    .map(student -> {
                        LoginResponse resp = new LoginResponse(true, "鐧诲綍鎴愬姛");
                        resp.setToken(JwtUtil.generateToken(student.getStudentNo(), student.getName()));
                        resp.setStudent(StudentInfo.fromStudent(student));
                        return ResponseEntity.ok(resp);
                    })
                    .orElse(ResponseEntity.ok(new LoginResponse(false, "瀛﹀彿鎴栧瘑鐮侀敊璇?)));
        }
    }
}

