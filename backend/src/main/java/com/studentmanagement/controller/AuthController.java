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
            return ResponseEntity.ok(new LoginResponse(false, "密码不能为空"));
        }

        String loginType = request.getLoginType() != null ? request.getLoginType() : "studentNo";

        if ("phone".equals(loginType)) {
            String phone = request.getPhone();
            if (phone == null || phone.isEmpty()) {
                return ResponseEntity.ok(new LoginResponse(false, "手机号不能为空"));
            }
            return studentRepository.findByPhoneAndPassword(phone, request.getPassword())
                    .map(student -> {
                        LoginResponse resp = new LoginResponse(true, "登录成功");
                        resp.setToken(JwtUtil.generateToken(student.getStudentNo(), student.getName()));
                        resp.setStudent(StudentInfo.fromStudent(student));
                        return ResponseEntity.ok(resp);
                    })
                    .orElse(ResponseEntity.ok(new LoginResponse(false, "手机号或密码错误")));
        } else {
            String studentNo = request.getStudentNo();
            if (studentNo == null || studentNo.isEmpty()) {
                return ResponseEntity.ok(new LoginResponse(false, "学号不能为空"));
            }
            return studentRepository.findByStudentNoAndPassword(studentNo, request.getPassword())
                    .map(student -> {
                        LoginResponse resp = new LoginResponse(true, "登录成功");
                        resp.setToken(JwtUtil.generateToken(student.getStudentNo(), student.getName()));
                        resp.setStudent(StudentInfo.fromStudent(student));
                        return ResponseEntity.ok(resp);
                    })
                    .orElse(ResponseEntity.ok(new LoginResponse(false, "学号或密码错误")));
        }
    }
}

