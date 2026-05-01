package com.onlinecourse.controller;

import com.onlinecourse.pojo.Role;
import com.onlinecourse.pojo.User;
import com.onlinecourse.repository.UserRepository;
import com.onlinecourse.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private UserRepository userRepository;

    private void checkAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Chưa đăng nhập!");
        }
        User user = userRepository.findByEmail(auth.getName());
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Chỉ Admin mới có quyền xem thống kê!");
        }
    }

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview() {
        try {
            checkAdminRole();
            return ResponseEntity.ok(statisticService.getOverview());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getCourseStatistics() {
        try {
            checkAdminRole();
            return ResponseEntity.ok(statisticService.getCourseStatistics());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
