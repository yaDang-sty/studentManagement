package com.studentmanagement.repository;

import com.studentmanagement.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Page<Score> findAll(Pageable pageable);
    Page<Score> findByStudentNameContaining(String studentName, Pageable pageable);
    Page<Score> findByCourseNameContaining(String courseName, Pageable pageable);
    Page<Score> findByStudentNo(String studentNo, Pageable pageable);
    Page<Score> findByCourseNo(String courseNo, Pageable pageable);
    List<Score> findByStudentNo(String studentNo);
    List<Score> findByStudentNameContaining(String studentName);
    List<Score> findByCourseNameContaining(String courseName);
    List<Score> findByCourseNo(String courseNo);
    List<Score> findByStudentNoAndCourseNo(String studentNo, String courseNo);
    List<Score> findByStudentNoAndSemester(String studentNo, String semester);
    List<Score> findByStudentNameContainingAndSemester(String studentName, String semester);

    @Query(nativeQuery = true, value =
        "SELECT s.grade AS grade, sc.course_name AS courseName, AVG(sc.score) AS avgScore " +
        "FROM score sc JOIN student s ON sc.student_no = s.student_no " +
        "WHERE s.grade IS NOT NULL AND s.grade != '' " +
        "GROUP BY s.grade, sc.course_name " +
        "ORDER BY s.grade, sc.course_name")
    List<Object[]> findAvgScoreByGradeAndCourse();

    @Query(nativeQuery = true, value =
        "SELECT s.class_name AS className, sc.course_name AS courseName, AVG(sc.score) AS avgScore " +
        "FROM score sc JOIN student s ON sc.student_no = s.student_no " +
        "WHERE s.grade = :grade AND s.class_name IS NOT NULL AND s.class_name != '' " +
        "GROUP BY s.class_name, sc.course_name " +
        "ORDER BY s.class_name, sc.course_name")
    List<Object[]> findAvgScoreByClassAndCourse(@Param("grade") String grade);
}

