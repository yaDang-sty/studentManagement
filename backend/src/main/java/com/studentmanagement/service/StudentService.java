package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Student;
import com.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("瀛︾敓涓嶅瓨鍦紝ID: " + id));
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
        // 鐢ㄧ涓€涓垎椤电粨鏋滆繑鍥?        return new PageResult<>(byName.getContent(), byName.getTotalElements(), page, pageSize);
    }

    public boolean existsByStudentNo(String studentNo) {
        return studentRepository.existsByStudentNo(studentNo);
    }
}

