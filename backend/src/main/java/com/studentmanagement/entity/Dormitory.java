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

    @NotBlank(message = "瀛﹀彿涓嶈兘涓虹┖")
    @Column(nullable = false, length = 20)
    private String studentNo;

    @NotBlank(message = "濮撳悕涓嶈兘涓虹┖")
    @Column(nullable = false, length = 50)
    private String studentName;

    @NotBlank(message = "妤兼爧涓嶈兘涓虹┖")
    @Column(nullable = false, length = 50)
    private String building;

    @NotBlank(message = "鎴块棿鍙蜂笉鑳戒负绌?)
    @Column(nullable = false, length = 20)
    private String room;

    @Column(length = 20)
    private String bed;

    @Column(length = 20)
    private String phone;
}

