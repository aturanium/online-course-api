package com.onlinecourse.repository;

import com.onlinecourse.pojo.Enrollment;
import java.util.List;

public interface EnrollmentRepository {

    void save(Enrollment enrollment);

    void delete(Enrollment enrollment);

    Enrollment findByCourseIdAndStudentId(Integer courseId, Integer studentId);

    List<Enrollment> findByStudentId(Integer studentId);

    Long countCompletedLessons(Integer studentId, Integer courseId);
}
