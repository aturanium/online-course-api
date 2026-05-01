package com.onlinecourse.service.impl;

import com.onlinecourse.dto.response.AdminOverviewResponse;
import com.onlinecourse.dto.response.CourseRevenueResponse;
import com.onlinecourse.pojo.Course;
import com.onlinecourse.repository.CourseRepository;
import com.onlinecourse.repository.TransactionRepository;
import com.onlinecourse.repository.UserRepository;
import com.onlinecourse.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public AdminOverviewResponse getOverview() {
        AdminOverviewResponse res = new AdminOverviewResponse();
        res.setTotalActiveCourses(courseRepository.countActiveCourses());
        res.setTotalStudents(userRepository.countStudents());
        res.setTotalRevenue(transactionRepository.calculateTotalRevenue());
        return res;
    }

    @Override
    public List<CourseRevenueResponse> getCourseStatistics() {

        Long totalStudentsInSystem = userRepository.countStudents();

        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(course -> {
            CourseRevenueResponse res = new CourseRevenueResponse();
            res.setCourseName(course.getName());
            res.setTeacherName(course.getTeacher().getLastName() + " " + course.getTeacher().getFirstName());

            int enrolledCount = course.getEnrollments() != null ? course.getEnrollments().size() : 0;
            res.setEnrolledStudents(enrolledCount);

            double frequency = 0.0;
            if (totalStudentsInSystem > 0) {
                frequency = (double) enrolledCount / totalStudentsInSystem;

                frequency = BigDecimal.valueOf(frequency).setScale(4, RoundingMode.HALF_UP).doubleValue();
            }
            res.setEnrollmentFrequency(frequency);

            res.setTotalRevenue(transactionRepository.calculateRevenueByCourse(course.getId()));

            return res;
        }).collect(Collectors.toList());
    }
}
