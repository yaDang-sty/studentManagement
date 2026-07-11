package com.studentmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @Column(length = 50)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType = UserType.VISITOR;

    public AppUser() {}

    public AppUser(String id, UserType userType) {
        this.id = id;
        this.userType = userType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
}