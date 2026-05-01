package com.onlinecourse.service;

import com.onlinecourse.dto.request.*;
import com.onlinecourse.dto.response.*;
import java.util.List;

public interface UserService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getMyProfile(String email);

    void updateProfile(String email, UpdateProfileRequest request);

    void changePassword(String email, ChangePasswordRequest request);

    List<AdminUserResponse> getAllUsers();

    void createUserByAdmin(AdminCreateUserRequest request);

    void updateUserByAdmin(Integer id, AdminUpdateUserRequest request);

    void deleteUser(Integer id);

    void resetPasswordByAdmin(Integer id, AdminResetPasswordRequest request);

    void verifyTeacher(Integer id);
}
