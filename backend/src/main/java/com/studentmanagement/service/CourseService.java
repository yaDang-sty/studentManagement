package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Course;
import com.studentmanagement.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public PageResult<Course> findAll(int page, int pageSize) {
        Page<Course> p = courseRepository.findAll(PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在，ID: " + id));
        existing.setCourseNo(course.getCourseNo());
        existing.setCourseName(course.getCourseName());
        existing.setTeacher(course.getTeacher());
        existing.setCredit(course.getCredit());
        existing.setClassroom(course.getClassroom());
        existing.setSchedule(course.getSchedule());
        return courseRepository.save(existing);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public PageResult<Course> search(String keyword, int page, int pageSize) {
        Page<Course> p = courseRepository.findByCourseNameContaining(keyword, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    
    public PageResult<Course> searchMultiple(String courseName, String courseNo, String teacher, int page, int pageSize) {
        Specification<Course> spec = (root, query, cb) -> {
            java.util.ArrayList<Predicate> predicates = new java.util.ArrayList<>();
            if (courseName != null && !courseName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("courseName"), "%" + courseName.trim() + "%"));
            }
            if (courseNo != null && !courseNo.trim().isEmpty()) {
                predicates.add(cb.like(root.get("courseNo"), "%" + courseNo.trim() + "%"));
            }
            if (teacher != null && !teacher.trim().isEmpty()) {
                predicates.add(cb.like(root.get("teacher"), "%" + teacher.trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Course> p = courseRepository.findAll(spec, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public boolean existsByCourseNo(String courseNo) {
        return courseRepository.existsByCourseNo(courseNo);
    }
}

