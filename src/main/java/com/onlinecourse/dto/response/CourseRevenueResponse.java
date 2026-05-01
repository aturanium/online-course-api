package com.onlinecourse.dto.response;

import java.math.BigDecimal;

public class CourseRevenueResponse {

    private String courseName;
    private String teacherName;
    private Integer enrolledStudents;
    private Double enrollmentFrequency;
    private BigDecimal totalRevenue;

    public CourseRevenueResponse() {
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Integer enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public Double getEnrollmentFrequency() {
        return enrollmentFrequency;
    }

    public void setEnrollmentFrequency(Double enrollmentFrequency) {
        this.enrollmentFrequency = enrollmentFrequency;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
