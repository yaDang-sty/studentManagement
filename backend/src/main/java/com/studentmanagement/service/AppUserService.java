package com.studentmanagement.service;

import com.studentmanagement.entity.AppUser;
import com.studentmanagement.entity.UserType;
import com.studentmanagement.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public Page<AppUser> findAll(int page, int pageSize) {
        return appUserRepository.findAll(PageRequest.of(page - 1, pageSize));
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    public Optional<AppUser> findById(String id) {
        return appUserRepository.findById(id);
    }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    public AppUser updateUserType(String id, UserType userType) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + id));
        user.setUserType(userType);
        return appUserRepository.save(user);
    }

    public Page<AppUser> search(String keyword, int page, int pageSize) {
        return appUserRepository.findAll(PageRequest.of(page - 1, pageSize));
    }

    public void deleteById(String id) {
        appUserRepository.deleteById(id);
    }

    public AppUser createDefaultUser(String studentNo) {
        AppUser user = new AppUser(studentNo, UserType.VISITOR);
        return appUserRepository.save(user);
    }
}