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

    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "学号不能为空")
    @Column(nullable = false, unique = true, length = 20)
    private String studentNo;

    @Column(length = 10)
    private String gender;

    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;

    @Column(length = 50)
    private String major;

    @Column(length = 20)
    private String grade;

    @Column(name = "class_name", length = 20)
    private String studentClass;

    @Email(message = "邮箱格式不正确")
    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String password;
}

