package com.onlinecourse.service;

import com.onlinecourse.dto.response.AdminOverviewResponse;
import com.onlinecourse.dto.response.CourseRevenueResponse;
import java.util.List;

public interface StatisticService {

    AdminOverviewResponse getOverview();

    List<CourseRevenueResponse> getCourseStatistics();
}
