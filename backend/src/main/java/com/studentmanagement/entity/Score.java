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

    @NotBlank(message = "瀛﹀彿涓嶈兘涓虹┖")
    @Column(nullable = false, length = 20)
    private String studentNo;

    @NotBlank(message = "濮撳悕涓嶈兘涓虹┖")
    @Column(nullable = false, length = 50)
    private String studentName;

    @NotBlank(message = "璇剧▼缂栧彿涓嶈兘涓虹┖")
    @Column(nullable = false, length = 20)
    private String courseNo;

    @NotBlank(message = "璇剧▼鍚嶇О涓嶈兘涓虹┖")
    @Column(nullable = false, length = 100)
    private String courseName;

    @NotNull(message = "鎴愮哗涓嶈兘涓虹┖")
    @Min(value = 0, message = "鎴愮哗涓嶈兘灏忎簬0")
    @Max(value = 100, message = "鎴愮哗涓嶈兘澶т簬100")
    @Column(nullable = false)
    private Integer score;

    @Column(length = 20)
    private String semester;
}

