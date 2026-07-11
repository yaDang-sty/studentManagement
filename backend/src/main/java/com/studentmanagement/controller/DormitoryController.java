package com.studentmanagement.controller;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Dormitory;
import com.studentmanagement.service.DormitoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dormitory")
@CrossOrigin(origins = "*")
public class DormitoryController {

    @Autowired
    private DormitoryService dormitoryService;

    @PostMapping("/list")
    public ResponseEntity<PageResult<Dormitory>> list(@RequestBody Map<String, Object> body) {
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(dormitoryService.findAll(page, pageSize));
    }

    @PostMapping("/getById")
    public ResponseEntity<Dormitory> getById(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        return dormitoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Dormitory dormitory) {
        if (dormitoryService.existsByStudentNo(dormitory.getStudentNo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "璇ュ鐢熷凡鍒嗛厤瀹胯垗"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dormitoryService.save(dormitory));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Dormitory dormitory) {
        try {
            Long id = dormitory.getId();
            if (id == null) return ResponseEntity.badRequest().body(Map.of("message", "缂哄皯ID"));
            return ResponseEntity.ok(dormitoryService.update(id, dormitory));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Long> body) {
        try {
            dormitoryService.deleteById(body.get("id"));
            return ResponseEntity.ok(Map.of("message", "鍒犻櫎鎴愬姛"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "瀹胯垗璁板綍涓嶅瓨鍦?));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<Dormitory>> search(@RequestBody Map<String, Object> body) {
        String keyword = (String) body.get("keyword");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(dormitoryService.search(keyword, page, pageSize));
    }
}

