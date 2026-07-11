package com.studentmanagement.entity;

public class ScoreStatsItem {
    private String grade;
    private String courseName;
    private Double avgScore;

    public ScoreStatsItem() {}

    public ScoreStatsItem(String grade, String courseName, Double avgScore) {
        this.grade = grade;
        this.courseName = courseName;
        this.avgScore = avgScore;
    }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Double getAvgScore() { return avgScore; }
    public void setAvgScore(Double avgScore) { this.avgScore = avgScore; }
}

