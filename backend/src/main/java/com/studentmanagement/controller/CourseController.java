package com.studentmanagement.controller;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Course;
import com.studentmanagement.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/list")
    public ResponseEntity<PageResult<Course>> list(@RequestBody Map<String, Object> body) {
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(courseService.findAll(page, pageSize));
    }

    @PostMapping("/getById")
    public ResponseEntity<Course> getById(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Course course) {
        if (courseService.existsByCourseNo(course.getCourseNo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "课程编号已存在"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(course));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Course course) {
        try {
            Long id = course.getId();
            if (id == null) return ResponseEntity.badRequest().body(Map.of("message", "缺少课程ID"));
            return ResponseEntity.ok(courseService.update(id, course));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    
    @PostMapping("/searchMultiple")
    public ResponseEntity<PageResult<Course>> searchMultiple(@RequestBody Map<String, Object> body) {
        String courseName = (String) body.get("courseName");
        String courseNo = (String) body.get("courseNo");
        String teacher = (String) body.get("teacher");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(courseService.searchMultiple(courseName, courseNo, teacher, page, pageSize));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Long> body) {
        try {
            courseService.deleteById(body.get("id"));
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "课程不存在"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<Course>> search(@RequestBody Map<String, Object> body) {
        String keyword = (String) body.get("keyword");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(courseService.search(keyword, page, pageSize));
    }
}

