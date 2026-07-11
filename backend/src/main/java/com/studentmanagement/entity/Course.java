package com.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "璇剧▼缂栧彿涓嶈兘涓虹┖")
    @Column(nullable = false, unique = true, length = 20)
    private String courseNo;

    @NotBlank(message = "璇剧▼鍚嶇О涓嶈兘涓虹┖")
    @Column(nullable = false, length = 100)
    private String courseName;

    @Column(length = 50)
    private String teacher;

    @Min(value = 1)
    @Max(value = 20)
    private Integer credit;

    @Column(length = 50)
    private String classroom;

    @Column(length = 100)
    private String schedule;
}

