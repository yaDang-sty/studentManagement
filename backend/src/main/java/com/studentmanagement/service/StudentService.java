package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Student;
import com.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public PageResult<Student> findAll(int page, int pageSize) {
        Page<Student> p = studentRepository.findAll(PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Student update(Long id, Student student) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + id));
        existing.setName(student.getName());
        existing.setStudentNo(student.getStudentNo());
        existing.setGender(student.getGender());
        existing.setAge(student.getAge());
        existing.setMajor(student.getMajor());
        existing.setGrade(student.getGrade());
        existing.setStudentClass(student.getStudentClass());
        existing.setEmail(student.getEmail());
        existing.setPhone(student.getPhone());
        existing.setAddress(student.getAddress());
        return studentRepository.save(existing);
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public PageResult<Student> search(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Student> byName = studentRepository.findByNameContaining(keyword, pageable);
        // 用第一个分页结果返回
        return new PageResult<>(byName.getContent(), byName.getTotalElements(), page, pageSize);
    }

    
    public PageResult<Student> searchMultiple(String name, String studentNo, String major, String grade, String studentClass, int page, int pageSize) {
        Specification<Student> spec = (root, query, cb) -> {
            java.util.ArrayList<Predicate> predicates = new java.util.ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (studentNo != null && !studentNo.trim().isEmpty()) {
                predicates.add(cb.like(root.get("studentNo"), "%" + studentNo.trim() + "%"));
            }
            if (major != null && !major.trim().isEmpty()) {
                predicates.add(cb.like(root.get("major"), "%" + major.trim() + "%"));
            }
            if (grade != null && !grade.trim().isEmpty()) {
                predicates.add(cb.like(root.get("grade"), "%" + grade.trim() + "%"));
            }
            if (studentClass != null && !studentClass.trim().isEmpty()) {
                predicates.add(cb.like(root.get("studentClass"), "%" + studentClass.trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Student> p = studentRepository.findAll(spec, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public boolean existsByStudentNo(String studentNo) {
        return studentRepository.existsByStudentNo(studentNo);
    }
}

