package com.studentmanagement.repository;

import com.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    Page<Student> findAll(Pageable pageable);
    Page<Student> findByNameContaining(String name, Pageable pageable);
    Page<Student> findByStudentNoContaining(String studentNo, Pageable pageable);
    Page<Student> findByMajorContaining(String major, Pageable pageable);
    Page<Student> findByGradeContaining(String grade, Pageable pageable);
    Page<Student> findByStudentClassContaining(String studentClass, Pageable pageable);
    List<Student> findByNameContaining(String name);
    List<Student> findByStudentNoContaining(String studentNo);
    List<Student> findByMajorContaining(String major);
    List<Student> findByGradeContaining(String grade);
    List<Student> findByStudentClassContaining(String studentClass);
    boolean existsByStudentNo(String studentNo);
    Optional<Student> findByStudentNoAndPassword(String studentNo, String password);
    Optional<Student> findByPhoneAndPassword(String phone, String password);
    Optional<Student> findByStudentNo(String studentNo);
}