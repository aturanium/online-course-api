package com.onlinecourse.service;

import com.onlinecourse.dto.request.CourseRequest;
import com.onlinecourse.dto.request.EnrollStudentRequest;
import com.onlinecourse.dto.request.LessonRequest;
import com.onlinecourse.dto.response.*;
import java.util.List;

public interface CourseService {

    void createCourse(String teacherEmail, CourseRequest request);

    void updateCourse(String teacherEmail, Integer courseId, CourseRequest request);

    void deleteCourse(Integer courseId, String email, String role);

    void approveCourse(Integer courseId);

    List<CourseResponse> getAllCoursesForAdmin();

    List<CourseResponse> getCoursesForTeacher(String teacherEmail);

    List<CourseResponse> searchActiveCourses(String keyword, Integer teacherId, String sortByPrice, String sortByName, Integer page);

    CourseDetailResponse getCourseDetail(Integer courseId, String currentUserEmail, String role);

    List<MyCourseResponse> getMyCourses(String studentEmail);

    void addLesson(String teacherEmail, Integer courseId, LessonRequest request);

    void updateLesson(String teacherEmail, Integer lessonId, LessonRequest request);

    void deleteLesson(String teacherEmail, Integer lessonId);

    void markLessonAsCompleted(String studentEmail, Integer lessonId);

    void enrollStudentByEmail(String teacherEmail, Integer courseId, EnrollStudentRequest request);

    void removeStudentFromCourse(String teacherEmail, Integer courseId, Integer studentId);

    List<StudentInCourseResponse> getStudentsInCourse(String teacherEmail, Integer courseId);

    StudentProgressResponse getStudentProgress(String teacherEmail, Integer courseId, Integer studentId);

    CourseStatisticResponse getCourseStatistic(String teacherEmail, Integer courseId);
}
