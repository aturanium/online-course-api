package com.onlinecourse.repository;

import com.onlinecourse.pojo.Course;
import com.onlinecourse.pojo.CourseStatus;
import java.util.List;

public interface CourseRepository {

    void save(Course course);

    void delete(Course course);

    Course findById(Integer id);

    List<Course> findAll();

    List<Course> findByTeacherId(Integer teacherId);

    List<Course> searchCourses(String keyword, Integer teacherId, CourseStatus status, String sortByPrice, String sortByName, Integer page);

    public Long countActiveCourses();
}
