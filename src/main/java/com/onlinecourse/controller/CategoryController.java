package com.onlinecourse.controller;

import com.onlinecourse.dto.request.CategoryRequest;
import com.onlinecourse.dto.response.CategoryResponse;
import com.onlinecourse.pojo.Role;
import com.onlinecourse.pojo.User;
import com.onlinecourse.repository.UserRepository;
import com.onlinecourse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    private void checkAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        User user = userRepository.findByEmail(auth.getName());
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này!");
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        try {
            checkAdminRole();
            categoryService.createCategory(request);
            return ResponseEntity.ok("Thêm chủ đề thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Integer id, @RequestBody CategoryRequest request) {
        try {
            checkAdminRole();
            categoryService.updateCategory(id, request);
            return ResponseEntity.ok("Cập nhật chủ đề thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) {
        try {
            checkAdminRole();
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Xóa chủ đề thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
