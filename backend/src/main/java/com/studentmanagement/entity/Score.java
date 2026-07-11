package com.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "score")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Column(nullable = false, length = 20)
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false, length = 50)
    private String studentName;

    @NotBlank(message = "课程编号不能为空")
    @Column(nullable = false, length = 20)
    private String courseNo;

    @NotBlank(message = "课程名称不能为空")
    @Column(nullable = false, length = 100)
    private String courseName;

    @NotNull(message = "成绩不能为空")
    @Min(value = 0, message = "成绩不能小于0")
    @Max(value = 100, message = "成绩不能大于100")
    @Column(nullable = false)
    private Integer score;

    @Column(length = 20)
    private String semester;
}

