package com.onlinecourse.dto.response;

import java.math.BigDecimal;

public class AdminOverviewResponse {

    private Long totalActiveCourses;
    private Long totalStudents;
    private BigDecimal totalRevenue;

    public AdminOverviewResponse() {
    }

    public Long getTotalActiveCourses() {
        return totalActiveCourses;
    }

    public void setTotalActiveCourses(Long totalActiveCourses) {
        this.totalActiveCourses = totalActiveCourses;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
