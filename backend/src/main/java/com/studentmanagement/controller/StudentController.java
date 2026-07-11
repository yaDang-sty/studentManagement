package com.studentmanagement.controller;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Student;
import com.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/list")
    public ResponseEntity<PageResult<Student>> list(@RequestBody Map<String, Object> body) {
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(studentService.findAll(page, pageSize));
    }

    @PostMapping("/getById")
    public ResponseEntity<Student> getById(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Student student) {
        if (studentService.existsByStudentNo(student.getStudentNo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "学号已存在"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.save(student));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Student student) {
        try {
            Long id = student.getId();
            if (id == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "缺少学生ID"));
            }
            return ResponseEntity.ok(studentService.update(id, student));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        try {
            studentService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "学生不存在"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<Student>> search(@RequestBody Map<String, Object> body) {
        String keyword = (String) body.get("keyword");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(studentService.search(keyword, page, pageSize));
    }
}

