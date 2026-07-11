package com.studentmanagement.controller;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.UserPassword;
import com.studentmanagement.repository.UserPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private UserPasswordRepository userPasswordRepository;

    @PostMapping("/list")
    public ResponseEntity<PageResult<UserPassword>> list(@RequestBody Map<String, Object> body) {
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        List<UserPassword> all = userPasswordRepository.findAll();
        int total = all.size();
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<UserPassword> records = from < total ? all.subList(from, to) : new ArrayList<>();
        return ResponseEntity.ok(new PageResult<>(records, total, page, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        String password = (String) body.get("password");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "账号不能为空"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "密码不能为空"));
        }
        id = id.trim();
        if (userPasswordRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(Map.of("message", "账号已存在"));
        }
        UserPassword saved = userPasswordRepository.save(new UserPassword(id, password));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        String password = (String) body.get("password");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "账号不能为空"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "密码不能为空"));
        }
        id = id.trim();
        Optional<UserPassword> opt = userPasswordRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "账号不存在"));
        }
        UserPassword up = opt.get();
        up.setPassword(password);
        UserPassword saved = userPasswordRepository.save(up);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "账号不能为空"));
        }
        try {
            userPasswordRepository.deleteById(id.trim());
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "账号不存在"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<UserPassword>> search(@RequestBody Map<String, Object> body) {
        String keyword = body.get("keyword") != null ? ((String) body.get("keyword")).trim().toLowerCase() : "";
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        List<UserPassword> all = userPasswordRepository.findAll();
        List<UserPassword> filtered = new ArrayList<>();
        for (UserPassword up : all) {
            if (keyword.isEmpty() || up.getId().toLowerCase().contains(keyword)) {
                filtered.add(up);
            }
        }
        int total = filtered.size();
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<UserPassword> records = from < total ? filtered.subList(from, to) : new ArrayList<>();
        return ResponseEntity.ok(new PageResult<>(records, total, page, pageSize));
    }
}