package com.onlinecourse.controller;

import com.onlinecourse.dto.request.*;
import com.onlinecourse.dto.response.AdminUserResponse;
import com.onlinecourse.dto.response.UserProfileResponse;
import com.onlinecourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        }
        return auth.getName();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            String email = getCurrentUserEmail();
            UserProfileResponse response = userService.getMyProfile(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@ModelAttribute UpdateProfileRequest request) {
        try {
            String email = getCurrentUserEmail();
            userService.updateProfile(email, request);
            return ResponseEntity.ok("Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp!");
            }
            String email = getCurrentUserEmail();
            userService.changePassword(email, request);
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUserByAdmin(@ModelAttribute AdminCreateUserRequest request) {
        try {
            userService.createUserByAdmin(request);
            return ResponseEntity.ok("Thêm người dùng thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable("id") Integer id, @ModelAttribute AdminUpdateUserRequest request) {
        try {
            userService.updateUserByAdmin(id, request);
            return ResponseEntity.ok("Cập nhật người dùng thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Xóa người dùng thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPasswordByAdmin(@PathVariable("id") Integer id, @RequestBody AdminResetPasswordRequest request) {
        try {
            userService.resetPasswordByAdmin(id, request);
            return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<?> verifyTeacher(@PathVariable("id") Integer id) {
        try {
            userService.verifyTeacher(id);
            return ResponseEntity.ok("Xác minh giảng viên thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
