package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Dormitory;
import com.studentmanagement.repository.DormitoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DormitoryService {

    @Autowired
    private DormitoryRepository dormitoryRepository;

    public PageResult<Dormitory> findAll(int page, int pageSize) {
        Page<Dormitory> p = dormitoryRepository.findAll(PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public Optional<Dormitory> findById(Long id) {
        return dormitoryRepository.findById(id);
    }

    public Dormitory save(Dormitory dormitory) {
        return dormitoryRepository.save(dormitory);
    }

    public Dormitory update(Long id, Dormitory dormitory) {
        Dormitory existing = dormitoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("瀹胯垗璁板綍涓嶅瓨鍦紝ID: " + id));
        existing.setStudentNo(dormitory.getStudentNo());
        existing.setStudentName(dormitory.getStudentName());
        existing.setBuilding(dormitory.getBuilding());
        existing.setRoom(dormitory.getRoom());
        existing.setBed(dormitory.getBed());
        existing.setPhone(dormitory.getPhone());
        return dormitoryRepository.save(existing);
    }

    public void deleteById(Long id) {
        dormitoryRepository.deleteById(id);
    }

    public PageResult<Dormitory> search(String keyword, int page, int pageSize) {
        Page<Dormitory> p = dormitoryRepository.findByStudentNameContaining(keyword, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public boolean existsByStudentNo(String studentNo) {
        return dormitoryRepository.existsByStudentNo(studentNo);
    }
}

