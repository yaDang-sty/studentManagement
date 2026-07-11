package com.studentmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_password")
public class UserPassword {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String password;

    public UserPassword() {}

    public UserPassword(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}