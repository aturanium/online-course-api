package com.onlinecourse.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.onlinecourse.dto.request.*;
import com.onlinecourse.dto.response.*;
import com.onlinecourse.pojo.Role;
import com.onlinecourse.pojo.User;
import com.onlinecourse.repository.UserRepository;
import com.onlinecourse.security.JwtUtil;
import com.onlinecourse.service.UserService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JwtUtil jwtUtil;

    private String uploadAvatar(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                return res.get("secure_url").toString();
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh lên hệ thống!");
            }
        }
        return null;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());

        if (request.getRole() == Role.TEACHER) {
            user.setIsVerified(false);
        } else {
            user.setIsVerified(true);
        }

        String avatarUrl = uploadAvatar(request.getAvatar());
        user.setAvatar(avatarUrl);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }

        String token = jwtUtil.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        UserProfileResponse response = new UserProfileResponse();
        response.setAvatar(user.getAvatar());
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole());
        response.setIsVerified(user.getIsVerified());
        response.setTransactionHistory(new ArrayList<>());

        return response;
    }

    @Override
    public void updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String newAvatarUrl = uploadAvatar(request.getAvatar());
            user.setAvatar(newAvatarUrl);
        }

        userRepository.save(user);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public List<AdminUserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            AdminUserResponse res = new AdminUserResponse();
            res.setId(user.getId());
            res.setAvatar(user.getAvatar());
            res.setFirstName(user.getFirstName());
            res.setLastName(user.getLastName());
            res.setEmail(user.getEmail());
            res.setRole(user.getRole());
            res.setIsVerified(user.getIsVerified());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public void createUserByAdmin(AdminCreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = uploadAvatar(request.getAvatar());
            user.setAvatar(avatarUrl);
        }

        user.setIsVerified(true);

        userRepository.save(user);
    }

    @Override
    public void updateUserByAdmin(Integer id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = uploadAvatar(request.getAvatar());
            user.setAvatar(avatarUrl);
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }
        userRepository.delete(user);
    }

    @Override
    public void resetPasswordByAdmin(Integer id, AdminResetPasswordRequest request) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void verifyTeacher(Integer id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }
        if (user.getRole() != Role.TEACHER) {
            throw new RuntimeException("Người dùng này không phải là Giảng viên!");
        }

        user.setIsVerified(true);
        userRepository.save(user);
    }
}
