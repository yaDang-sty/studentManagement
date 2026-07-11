package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Score;
import com.studentmanagement.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public PageResult<Score> findAll(int page, int pageSize) {
        Page<Score> p = scoreRepository.findAll(PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public Optional<Score> findById(Long id) {
        return scoreRepository.findById(id);
    }

    public Score save(Score score) {
        return scoreRepository.save(score);
    }

    public Score update(Long id, Score score) {
        Score existing = scoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("成绩记录不存在，ID: " + id));
        existing.setStudentNo(score.getStudentNo());
        existing.setStudentName(score.getStudentName());
        existing.setCourseNo(score.getCourseNo());
        existing.setCourseName(score.getCourseName());
        existing.setScore(score.getScore());
        existing.setSemester(score.getSemester());
        return scoreRepository.save(existing);
    }

    public void deleteById(Long id) {
        scoreRepository.deleteById(id);
    }

    
    public PageResult<Score> searchMultiple(String studentName, String courseName, int page, int pageSize) {
        Specification<Score> spec = (root, query, cb) -> {
            java.util.ArrayList<Predicate> predicates = new java.util.ArrayList<>();
            if (studentName != null && !studentName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("studentName"), "%" + studentName.trim() + "%"));
            }
            if (courseName != null && !courseName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("courseName"), "%" + courseName.trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Score> p = scoreRepository.findAll(spec, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public PageResult<Score> search(String keyword, int page, int pageSize) {
        Page<Score> p = scoreRepository.findByStudentNameContaining(keyword, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public PageResult<Score> findByStudentNo(String studentNo, int page, int pageSize) {
        Page<Score> p = scoreRepository.findByStudentNo(studentNo, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public PageResult<Score> findByStudentNoAndSemester(String studentNo, String semester, int page, int pageSize) {
        Page<Score> p;
        if (semester != null && !semester.trim().isEmpty()) {
            p = scoreRepository.findByStudentNoAndSemester(studentNo, semester.trim(), PageRequest.of(page - 1, pageSize));
        } else {
            p = scoreRepository.findByStudentNo(studentNo, PageRequest.of(page - 1, pageSize));
        }
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public PageResult<Score> findByCourseNo(String courseNo, int page, int pageSize) {
        Page<Score> p = scoreRepository.findByCourseNo(courseNo, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }
}

