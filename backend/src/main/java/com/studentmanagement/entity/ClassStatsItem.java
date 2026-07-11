package com.studentmanagement.entity;

public class ClassStatsItem {
    private String className;
    private String courseName;
    private Double avgScore;

    public ClassStatsItem() {}

    public ClassStatsItem(String className, String courseName, Double avgScore) {
        this.className = className;
        this.courseName = courseName;
        this.avgScore = avgScore;
    }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Double getAvgScore() { return avgScore; }
    public void setAvgScore(Double avgScore) { this.avgScore = avgScore; }
}

