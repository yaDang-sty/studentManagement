package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Course;
import com.studentmanagement.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("璇剧▼涓嶅瓨鍦紝ID: " + id));
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

    public boolean existsByCourseNo(String courseNo) {
        return courseRepository.existsByCourseNo(courseNo);
    }
}

