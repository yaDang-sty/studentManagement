package com.studentmanagement.entity;

public class StudentInfo {
    private Long id;
    private String name;
    private String studentNo;
    private String gender;
    private Integer age;
    private String grade;
    private String studentClass;
    private String major;
    private String email;
    private String phone;

    public static StudentInfo fromStudent(Student s) {
        StudentInfo info = new StudentInfo();
        info.id = s.getId();
        info.name = s.getName();
        info.studentNo = s.getStudentNo();
        info.gender = s.getGender();
        info.age = s.getAge();
        info.grade = s.getGrade();
        info.studentClass = s.getStudentClass();
        info.major = s.getMajor();
        info.email = s.getEmail();
        info.phone = s.getPhone();
        return info;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentNo() { return studentNo; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getGrade() { return grade; }
    public String getStudentClass() { return studentClass; }
    public String getMajor() { return major; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

