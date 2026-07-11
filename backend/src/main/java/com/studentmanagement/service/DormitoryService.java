package com.studentmanagement.service;

import com.studentmanagement.entity.PageResult;
import com.studentmanagement.entity.Dormitory;
import com.studentmanagement.repository.DormitoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

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
                .orElseThrow(() -> new RuntimeException("宿舍记录不存在，ID: " + id));
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

    
    public PageResult<Dormitory> searchMultiple(String studentName, String building, String room, int page, int pageSize) {
        Specification<Dormitory> spec = (root, query, cb) -> {
            java.util.ArrayList<Predicate> predicates = new java.util.ArrayList<>();
            if (studentName != null && !studentName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("studentName"), "%" + studentName.trim() + "%"));
            }
            if (building != null && !building.trim().isEmpty()) {
                predicates.add(cb.like(root.get("building"), "%" + building.trim() + "%"));
            }
            if (room != null && !room.trim().isEmpty()) {
                predicates.add(cb.like(root.get("room"), "%" + room.trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Dormitory> p = dormitoryRepository.findAll(spec, PageRequest.of(page - 1, pageSize));
        return new PageResult<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    public boolean existsByStudentNo(String studentNo) {
        return dormitoryRepository.existsByStudentNo(studentNo);
    }
}

