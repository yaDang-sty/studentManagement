package com.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "dormitory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dormitory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Column(nullable = false, length = 20)
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false, length = 50)
    private String studentName;

    @NotBlank(message = "楼栋不能为空")
    @Column(nullable = false, length = 50)
    private String building;

    @NotBlank(message = "房间号不能为空")
    @Column(nullable = false, length = 20)
    private String room;

    @Column(length = 20)
    private String bed;

    @Column(length = 20)
    private String phone;
}

