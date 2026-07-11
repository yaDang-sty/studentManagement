package com.studentmanagement.repository;

import com.studentmanagement.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findAll(Pageable pageable);
    Page<Course> findByCourseNameContaining(String courseName, Pageable pageable);
    Page<Course> findByTeacherContaining(String teacher, Pageable pageable);
    Page<Course> findByCourseNoContaining(String courseNo, Pageable pageable);
    List<Course> findByCourseNameContaining(String courseName);
    List<Course> findByTeacherContaining(String teacher);
    List<Course> findByCourseNoContaining(String courseNo);
    boolean existsByCourseNo(String courseNo);
}

