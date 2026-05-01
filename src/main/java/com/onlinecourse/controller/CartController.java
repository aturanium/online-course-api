package com.onlinecourse.controller;

import com.onlinecourse.dto.request.PaymentRequest;
import com.onlinecourse.dto.request.UpdatePaymentRequest;
import com.onlinecourse.pojo.Role;
import com.onlinecourse.pojo.User;
import com.onlinecourse.repository.UserRepository;
import com.onlinecourse.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }
        User user = userRepository.findByEmail(auth.getName());
        if (user == null || user.getRole() != Role.STUDENT) {
            throw new RuntimeException("Chỉ sinh viên mới được sử dụng tính năng này!");
        }
        return user;
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart() {
        try {
            User student = getAuthenticatedStudent();
            return ResponseEntity.ok(cartService.getCart(student.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addToCart(@RequestParam("courseId") Integer courseId) {
        try {
            User student = getAuthenticatedStudent();
            cartService.addToCart(student.getEmail(), courseId);
            return ResponseEntity.ok("Đã thêm khóa học vào giỏ hàng!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/{courseId}")
    public ResponseEntity<?> removeFromCart(@PathVariable("courseId") Integer courseId) {
        try {
            User student = getAuthenticatedStudent();
            cartService.removeFromCart(student.getEmail(), courseId);
            return ResponseEntity.ok("Đã xóa khóa học khỏi giỏ hàng!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/payment/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            User student = getAuthenticatedStudent();
            Integer transactionId = cartService.createPayment(student.getEmail(), request);

            return ResponseEntity.ok("{\"transactionId\": " + transactionId + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/payment/{id}")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable("id") Integer transactionId,
            @RequestBody UpdatePaymentRequest request) {
        try {
            User student = getAuthenticatedStudent();
            cartService.updatePaymentStatus(student.getEmail(), transactionId, request.getStatus());

            if ("SUCCESS".equals(request.getStatus())) {
                return ResponseEntity.ok("Thanh toán thành công! Bạn có thể bắt đầu học ngay.");
            } else {
                return ResponseEntity.ok("Thanh toán thất bại hoặc đã bị hủy.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/transaction")
    public ResponseEntity<?> getTransactionHistory() {
        try {
            User student = getAuthenticatedStudent();
            return ResponseEntity.ok(cartService.getTransactionHistory(student.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
