import os

content = """package com.studentmanagement.controller;

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
        String keyword = (String) body.get("keyword");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        Page<AppUser> p = appUserService.search(keyword, page, pageSize);
        List<Map<String, Object>> records = new ArrayList<>();
        for (AppUser user : p.getContent()) {
            records.add(toUserMap(user));
        }
        return ResponseEntity.ok(new PageResult<>(records, p.getTotalElements(), page, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Long studentId = body.get("studentId") != null ? ((Number) body.get("studentId")).longValue() : null;
        if (studentId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "\u7f3a\u5c11\u5b66\u751fID"));
        }
        if (!studentRepository.existsById(studentId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "\u5b66\u751f\u4e0d\u5b58\u5728"));
        }
        if (appUserService.findById(studentId).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "\u8be5\u5b66\u751f\u5df2\u6709\u7528\u6237\u8bb0\u5f55"));
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
        Long id = body.get("id") != null ? ((Number) body.get("id")).longValue() : null;
        String typeStr = (String) body.get("userType");
        if (id == null || typeStr == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "\u7f3a\u5c11\u7528\u6237ID\u6216\u7528\u6237\u7c7b\u578b"));
        }
        try {
            UserType userType = UserType.valueOf(typeStr);
            return ResponseEntity.ok(toUserMap(appUserService.updateUserType(id, userType)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "\u65e0\u6548\u7684\u7528\u6237\u7c7b\u578b"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> body) {
        Long id = body.get("id") != null ? ((Number) body.get("id")).longValue() : null;
        if (id == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "\u7f3a\u5c11\u7528\u6237ID"));
        }
        try {
            appUserService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "\u5220\u9664\u6210\u529f"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "\u7528\u6237\u4e0d\u5b58\u5728"));
        }
    }

    @PostMapping("/studentsWithoutUser")
    public ResponseEntity<List<Map<String, Object>>> getStudentsWithoutUser() {
        List<Student> allStudents = studentRepository.findAll();
        List<Long> existingUserIds = appUserService.findAll().stream()
                .map(AppUser::getId).collect(Collectors.toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : allStudents) {
            if (!existingUserIds.contains(s.getId())) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", s.getId());
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
        Optional<Student> stuOpt = studentRepository.findById(user.getId());
        if (stuOpt.isPresent()) {
            Student stu = stuOpt.get();
            map.put("name", stu.getName());
            map.put("studentNo", stu.getStudentNo());
        } else {
            map.put("name", "\u5df2\u5220\u9664");
            map.put("studentNo", String.valueOf(user.getId()));
        }
        return map;
    }
}
"""

with open("backend/src/main/java/com/studentmanagement/controller/AppUserController.java", "w", encoding="utf-8") as f:
    f.write(content)
print("AppUserController.java written")
