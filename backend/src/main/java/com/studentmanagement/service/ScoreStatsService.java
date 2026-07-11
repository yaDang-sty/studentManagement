package com.studentmanagement.service;

import com.studentmanagement.entity.ClassStatsItem;
import com.studentmanagement.entity.Score;
import com.studentmanagement.entity.ScoreStatsItem;
import com.studentmanagement.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreStatsService {

    @Autowired
    private ScoreRepository scoreRepository;

    public List<ScoreStatsItem> getAvgScoreByGradeAndCourse() {
        List<Object[]> rows = scoreRepository.findAvgScoreByGradeAndCourse();
        List<ScoreStatsItem> result = new ArrayList<>();
        for (Object[] row : rows) {
            String grade = (String) row[0];
            String courseName = (String) row[1];
            Double avgScore = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            result.add(new ScoreStatsItem(grade, courseName, avgScore));
        }
        return result;
    }

    public List<Score> getStudentScores(String query, String semester) {
        // query by studentNo first, then by name
        List<Score> results = scoreRepository.findByStudentNo(query);
        if (results.isEmpty()) {
            results = scoreRepository.findByStudentNameContaining(query);
        }
        // filter by semester if provided
        if (semester != null && !semester.isEmpty()) {
            results = results.stream()
                .filter(s -> semester.equals(s.getSemester()))
                .collect(java.util.stream.Collectors.toList());
        }
        return results;
    }

    public List<ClassStatsItem> getAvgScoreByClass(String grade) {
        List<Object[]> rows = scoreRepository.findAvgScoreByClassAndCourse(grade);
        List<ClassStatsItem> result = new ArrayList<>();
        for (Object[] row : rows) {
            String className = (String) row[0];
            String courseName = (String) row[1];
            Double avgScore = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            result.add(new ClassStatsItem(className, courseName, avgScore));
        }
        return result;
    }
}

