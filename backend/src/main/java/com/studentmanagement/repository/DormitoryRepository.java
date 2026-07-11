package com.studentmanagement.repository;

import com.studentmanagement.entity.Dormitory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory, Long>, JpaSpecificationExecutor<Dormitory> {
    Page<Dormitory> findAll(Pageable pageable);
    Page<Dormitory> findByStudentNameContaining(String studentName, Pageable pageable);
    Page<Dormitory> findByBuildingContaining(String building, Pageable pageable);
    Page<Dormitory> findByRoomContaining(String room, Pageable pageable);
    List<Dormitory> findByStudentNameContaining(String studentName);
    List<Dormitory> findByBuildingContaining(String building);
    List<Dormitory> findByRoomContaining(String room);
    boolean existsByStudentNo(String studentNo);
}

