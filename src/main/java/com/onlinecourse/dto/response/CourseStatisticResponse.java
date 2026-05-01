package com.onlinecourse.dto.response;

import java.math.BigDecimal;

public class CourseStatisticResponse {

    private Integer totalStudents;
    private BigDecimal totalRevenue;

    public CourseStatisticResponse() {
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
