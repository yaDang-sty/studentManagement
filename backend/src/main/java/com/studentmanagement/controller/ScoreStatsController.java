package com.studentmanagement.controller;

import com.studentmanagement.entity.ClassStatsItem;
import com.studentmanagement.entity.Score;
import com.studentmanagement.entity.ScoreStatsItem;
import com.studentmanagement.service.ScoreStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class ScoreStatsController {

    @Autowired
    private ScoreStatsService scoreStatsService;

    @PostMapping("/avg-by-grade")
    public ResponseEntity<List<ScoreStatsItem>> avgByGrade(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(scoreStatsService.getAvgScoreByGradeAndCourse());
    }

    @PostMapping("/student-scores")
    public ResponseEntity<List<Score>> studentScores(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        String semester = body.get("semester");
        return ResponseEntity.ok(scoreStatsService.getStudentScores(query, semester));
    }

    @PostMapping("/avg-by-class")
    public ResponseEntity<List<ClassStatsItem>> avgByClass(@RequestBody Map<String, String> body) {
        String grade = body.get("grade");
        return ResponseEntity.ok(scoreStatsService.getAvgScoreByClass(grade));
    }
}

