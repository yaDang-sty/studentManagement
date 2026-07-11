package com.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "濮撳悕涓嶈兘涓虹┖")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "瀛﹀彿涓嶈兘涓虹┖")
    @Column(nullable = false, unique = true, length = 20)
    private String studentNo;

    @Column(length = 10)
    private String gender;

    @Min(value = 1, message = "骞撮緞蹇呴』澶т簬0")
    @Max(value = 150, message = "骞撮緞蹇呴』灏忎簬150")
    private Integer age;

    @Column(length = 50)
    private String major;

    @Column(length = 20)
    private String grade;

    @Column(name = "class_name", length = 20)
    private String studentClass;

    @Email(message = "閭鏍煎紡涓嶆纭?)
    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String password;
}

