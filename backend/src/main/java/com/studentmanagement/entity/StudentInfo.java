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
    private String userType;

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
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
