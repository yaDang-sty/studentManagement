package com.studentmanagement.entity;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private StudentInfo student;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public StudentInfo getStudent() { return student; }
    public void setStudent(StudentInfo student) { this.student = student; }
}
