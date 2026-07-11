package com.studentmanagement.controller;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Score;
import com.studentmanagement.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/score")
@CrossOrigin(origins = "*")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @PostMapping("/list")
    public ResponseEntity<PageResult<Score>> list(@RequestBody Map<String, Object> body) {
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(scoreService.findAll(page, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Score score) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scoreService.save(score));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Score score) {
        try {
            Long id = score.getId();
            if (id == null) return ResponseEntity.badRequest().body(Map.of("message", "缺少成绩ID"));
            return ResponseEntity.ok(scoreService.update(id, score));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Long> body) {
        try {
            scoreService.deleteById(body.get("id"));
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "成绩记录不存在"));
        }
    }


    @PostMapping("/searchMultiple")
    public ResponseEntity<PageResult<Score>> searchMultiple(@RequestBody Map<String, Object> body) {
        String studentName = (String) body.get("studentName");
        String courseName = (String) body.get("courseName");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(scoreService.searchMultiple(studentName, courseName, page, pageSize));
    }

    @PostMapping("/search")
    public ResponseEntity<PageResult<Score>> search(@RequestBody Map<String, Object> body) {
        String keyword = (String) body.get("keyword");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        return ResponseEntity.ok(scoreService.search(keyword, page, pageSize));
    }

    @PostMapping("/query")
    public ResponseEntity<?> query(@RequestBody Map<String, Object> body) {
        String studentNo = (String) body.get("studentNo");
        String courseNo = (String) body.get("courseNo");
        String semester = (String) body.get("semester");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        if (studentNo != null && !studentNo.isEmpty()) {
            if (semester != null && !semester.trim().isEmpty()) {
                return ResponseEntity.ok(scoreService.findByStudentNoAndSemester(studentNo, semester.trim(), page, pageSize));
            }
            return ResponseEntity.ok(scoreService.findByStudentNo(studentNo, page, pageSize));
        }
        if (courseNo != null && !courseNo.isEmpty()) {
            return ResponseEntity.ok(scoreService.findByCourseNo(courseNo, page, pageSize));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "??????????"));
    }

}