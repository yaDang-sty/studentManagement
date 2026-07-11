package com.studentmanagement.config;

import com.studentmanagement.entity.AppUser;
import com.studentmanagement.entity.UserPassword;
import com.studentmanagement.entity.UserType;
import com.studentmanagement.repository.AppUserRepository;
import com.studentmanagement.repository.UserPasswordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private UserPasswordRepository userPasswordRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostConstruct
    public void init() {
        if (!userPasswordRepository.existsById("admin")) {
            userPasswordRepository.save(new UserPassword("admin", "admin123"));
        }
        if (!appUserRepository.existsById("admin")) {
            appUserRepository.save(new AppUser("admin", UserType.TECH_ADMIN));
        }
    }
}