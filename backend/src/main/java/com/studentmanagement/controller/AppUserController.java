package com.studentmanagement.controller;

import com.studentmanagement.entity.AppUser;
import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Student;
import com.studentmanagement.entity.UserType;
import com.studentmanagement.repository.StudentRepository;
import com.studentmanagement.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/list")
    public ResponseEntity<PageResult<Map<String, Object>>> list(@RequestBody Map<String, Object> body) {
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        Page<AppUser> p = appUserService.findAll(page, pageSize);
        List<Map<String, Object>> records = new ArrayList<>();
        for (AppUser user : p.getContent()) {
            records.add(toUserMap(user));
        }
        return ResponseEntity.ok(new PageResult<>(records, p.getTotalElements(), page, pageSize));
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<Map<String, Object>>> search(@RequestBody Map<String, Object> body) {
        String keyword = body.get("keyword") != null ? ((String) body.get("keyword")).toLowerCase() : "";
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        List<AppUser> allUsers = appUserService.findAll();
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (AppUser user : allUsers) {
            Map<String, Object> m = toUserMap(user);
            String name = (String) m.getOrDefault("name", "");
            String studentNo = (String) m.getOrDefault("studentNo", "");
            String typeDisplay = (String) m.getOrDefault("userTypeDisplay", "");
            if (keyword.isEmpty() || name.toLowerCase().contains(keyword) || studentNo.contains(keyword) || typeDisplay.contains(keyword)) {
                filtered.add(m);
            }
        }
        int total = filtered.size();
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> records = from < total ? filtered.subList(from, to) : new ArrayList<>();
        return ResponseEntity.ok(new PageResult<>(records, total, page, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String studentId = (String) body.get("studentId");
        if (studentId == null || studentId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少学生学号"));
        }
        studentId = studentId.trim();
        if (!studentRepository.findByStudentNo(studentId).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "学生不存在"));
        }
        if (appUserService.findById(studentId).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "该学生已有用户记录"));
        }
        String typeStr = (String) body.get("userType");
        UserType userType = UserType.VISITOR;
        if (typeStr != null) {
            try { userType = UserType.valueOf(typeStr); } catch (IllegalArgumentException ignored) {}
        }
        AppUser saved = appUserService.createDefaultUser(studentId);
        if (userType != UserType.VISITOR) {
            saved = appUserService.updateUserType(studentId, userType);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserMap(saved));
    }

    @PostMapping("/updateUserType")
    public ResponseEntity<?> updateUserType(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        String typeStr = (String) body.get("userType");
        if (id == null || id.trim().isEmpty() || typeStr == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少用户ID或用户类型"));
        }
        try {
            UserType userType = UserType.valueOf(typeStr);
            return ResponseEntity.ok(toUserMap(appUserService.updateUserType(id.trim(), userType)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "无效的用户类型"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少用户ID"));
        }
        try {
            appUserService.deleteById(id.trim());
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "用户不存在"));
        }
    }

    @PostMapping("/studentsWithoutUser")
    public ResponseEntity<List<Map<String, Object>>> getStudentsWithoutUser() {
        List<Student> allStudents = studentRepository.findAll();
        Set<String> existingUserIds = appUserService.findAll().stream()
                .map(AppUser::getId).collect(Collectors.toSet());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : allStudents) {
            if (!existingUserIds.contains(s.getStudentNo())) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", s.getStudentNo());
                m.put("studentNo", s.getStudentNo());
                m.put("name", s.getName());
                result.add(m);
            }
        }
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toUserMap(AppUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("userType", user.getUserType().name());
        map.put("userTypeDisplay", user.getUserType().getDisplayName());
        // 用学生学号查找学生信息
        Optional<Student> stuOpt = studentRepository.findByStudentNo(user.getId());
        if (stuOpt.isPresent()) {
            Student stu = stuOpt.get();
            map.put("name", stu.getName());
            map.put("studentNo", stu.getStudentNo());
        } else {
            map.put("name", user.getId());
            map.put("studentNo", user.getId());
        }
        return map;
    }
}