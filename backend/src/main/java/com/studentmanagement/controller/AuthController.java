package com.studentmanagement.controller;

import com.studentmanagement.entity.*;
import com.studentmanagement.repository.StudentRepository;
import com.studentmanagement.repository.UserPasswordRepository;
import com.studentmanagement.service.AppUserService;
import com.studentmanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserPasswordRepository userPasswordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String account = request.getStudentNo();
        String password = request.getPassword();

        if (account == null || account.trim().isEmpty()) {
            return ResponseEntity.ok(new LoginResponse(false, "账号不能为空"));
        }
        if (password == null || password.isEmpty()) {
            return ResponseEntity.ok(new LoginResponse(false, "密码不能为空"));
        }

        account = account.trim();

        // 查询 user_password 表验证密码
        Optional<UserPassword> upOpt = userPasswordRepository.findByIdAndPassword(account, password);
        if (upOpt.isPresent()) {
            Optional<Student> stuOpt = studentRepository.findByStudentNo(account);
            if (stuOpt.isPresent()) {
                Student student = stuOpt.get();
                LoginResponse resp = new LoginResponse(true, "登录成功");
                resp.setToken(JwtUtil.generateToken(student.getStudentNo(), student.getName()));
                StudentInfo info = StudentInfo.fromStudent(student);
                try { appUserService.createDefaultUser(student.getStudentNo()); } catch (Exception ignored) {}
                Optional<AppUser> appUser = appUserService.findById(student.getStudentNo());
                info.setUserType(appUser.map(u -> u.getUserType().name()).orElse("VISITOR"));
                resp.setStudent(info);
                return ResponseEntity.ok(resp);
            } else {
                // 非学生账号（如管理员）
                LoginResponse resp = new LoginResponse(true, "登录成功");
                resp.setToken(JwtUtil.generateToken(account, account));
                StudentInfo info = new StudentInfo();
                info.setStudentNo(account);
                info.setName(account);
                Optional<AppUser> appUser = appUserService.findById(account);
                info.setUserType(appUser.map(u -> u.getUserType().name()).orElse("TECH_ADMIN"));
                resp.setStudent(info);
                return ResponseEntity.ok(resp);
            }
        }

        // 兼容旧数据：已有学生首次登录自动创建密码
        Optional<Student> stuOpt = studentRepository.findByStudentNo(account);
        if (stuOpt.isPresent()) {
            userPasswordRepository.save(new UserPassword(account, password));
            Student student = stuOpt.get();
            LoginResponse resp = new LoginResponse(true, "登录成功");
            resp.setToken(JwtUtil.generateToken(student.getStudentNo(), student.getName()));
            StudentInfo info = StudentInfo.fromStudent(student);
            try { appUserService.createDefaultUser(student.getStudentNo()); } catch (Exception ignored) {}
            Optional<AppUser> appUser = appUserService.findById(student.getStudentNo());
            info.setUserType(appUser.map(u -> u.getUserType().name()).orElse("VISITOR"));
            resp.setStudent(info);
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.ok(new LoginResponse(false, "账号或密码错误"));
    }
}