package com.onlinecourse.controller;

import com.onlinecourse.dto.request.CourseRequest;
import com.onlinecourse.dto.request.EnrollStudentRequest;
import com.onlinecourse.pojo.Role;
import com.onlinecourse.pojo.User;
import com.onlinecourse.repository.UserRepository;
import com.onlinecourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByEmail(auth.getName());
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getCourses(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "teacherId", required = false) Integer teacherId,
            @RequestParam(value = "sortByPrice", required = false) String sortByPrice,
            @RequestParam(value = "sortByName", required = false) String sortByName,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {

        User user = getCurrentUser();

        if (user != null && user.getRole() == Role.ADMIN) {
            return ResponseEntity.ok(courseService.getAllCoursesForAdmin());
        } else if (user != null && user.getRole() == Role.TEACHER) {
            return ResponseEntity.ok(courseService.getCoursesForTeacher(user.getEmail()));
        } else {
            return ResponseEntity.ok(courseService.searchActiveCourses(keyword, teacherId, sortByPrice, sortByName, page));
        }
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getCourseDetail(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            String email = (user != null) ? user.getEmail() : null;
            String role = (user != null) ? user.getRole().name() : "GUEST";

            return ResponseEntity.ok(courseService.getCourseDetail(id, email, role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@ModelAttribute CourseRequest request) {
        try {
            User user = getCurrentUser();
            if (user == null || user.getRole() != Role.TEACHER) {
                return ResponseEntity.status(403).body("Không có quyền!");
            }

            courseService.createCourse(user.getEmail(), request);
            return ResponseEntity.ok("Thêm khóa học thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable("id") Integer id, @ModelAttribute CourseRequest request) {
        try {
            User user = getCurrentUser();
            courseService.updateCourse(user.getEmail(), id, request);
            return ResponseEntity.ok("Cập nhật khóa học thành công, đang chờ duyệt!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            if (user == null || (user.getRole() != Role.TEACHER && user.getRole() != Role.ADMIN)) {
                return ResponseEntity.status(403).body("Không có quyền!");
            }
            courseService.deleteCourse(id, user.getEmail(), user.getRole().name());
            return ResponseEntity.ok("Xóa khóa học thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/courses/{id}/students")
    public ResponseEntity<?> enrollStudent(@PathVariable("id") Integer id, @RequestBody EnrollStudentRequest request) {
        try {
            User user = getCurrentUser();
            courseService.enrollStudentByEmail(user.getEmail(), id, request);
            return ResponseEntity.ok("Thêm sinh viên thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/courses/{id}/students/{studentId}")
    public ResponseEntity<?> removeStudent(@PathVariable("id") Integer courseId, @PathVariable("studentId") Integer studentId) {
        try {
            User user = getCurrentUser();
            courseService.removeStudentFromCourse(user.getEmail(), courseId, studentId);
            return ResponseEntity.ok("Đã gỡ sinh viên khỏi khóa học!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/courses/{id}/students")
    public ResponseEntity<?> getStudentsInCourse(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            return ResponseEntity.ok(courseService.getStudentsInCourse(user.getEmail(), id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/courses/{id}/students/{studentId}")
    public ResponseEntity<?> getStudentProgress(@PathVariable("id") Integer courseId, @PathVariable("studentId") Integer studentId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(courseService.getStudentProgress(user.getEmail(), courseId, studentId));
    }

    @GetMapping("/courses/{id}/statistic")
    public ResponseEntity<?> getCourseStatistic(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            return ResponseEntity.ok(courseService.getCourseStatistic(user.getEmail(), id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/courses/{id}/verify")
    public ResponseEntity<?> approveCourse(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            if (user == null || user.getRole() != Role.ADMIN) {
                return ResponseEntity.status(403).body("Không có quyền!");
            }

            courseService.approveCourse(id);
            return ResponseEntity.ok("Duyệt khóa học thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/student/courses")
    public ResponseEntity<?> getMyCourses() {
        User user = getCurrentUser();
        return ResponseEntity.ok(courseService.getMyCourses(user.getEmail()));
    }
}
