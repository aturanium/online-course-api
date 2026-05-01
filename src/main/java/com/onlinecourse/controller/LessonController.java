package com.onlinecourse.controller;

import com.onlinecourse.dto.request.LessonRequest;
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
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName());
    }

    @PostMapping
    public ResponseEntity<?> addLesson(@RequestBody LessonRequest request) {
        try {
            User user = getCurrentUser();
            courseService.addLesson(user.getEmail(), request.getCourseId(), request);
            return ResponseEntity.ok("Thêm bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable("id") Integer id, @RequestBody LessonRequest request) {
        try {
            User user = getCurrentUser();
            courseService.updateLesson(user.getEmail(), id, request);
            return ResponseEntity.ok("Cập nhật bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            courseService.deleteLesson(user.getEmail(), id);
            return ResponseEntity.ok("Xóa bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> markCompleted(@PathVariable("id") Integer id) {
        try {
            User user = getCurrentUser();
            if (user.getRole() != Role.STUDENT) {
                throw new RuntimeException("Chỉ sinh viên mới có tiến độ học tập!");
            }

            courseService.markLessonAsCompleted(user.getEmail(), id);
            return ResponseEntity.ok("Đã cập nhật tiến độ!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
