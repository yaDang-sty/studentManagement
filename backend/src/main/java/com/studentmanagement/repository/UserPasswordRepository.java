package com.studentmanagement.repository;

import com.studentmanagement.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, String> {
    Optional<UserPassword> findByIdAndPassword(String id, String password);
    boolean existsById(String id);
}