package com.tatvasoft.interview_portal.service;

import com.tatvasoft.interview_portal.dto.UserRequest;
import com.tatvasoft.interview_portal.dto.UserResponse;
import com.tatvasoft.interview_portal.entity.User;

import java.util.List;

public interface UserService {
    User login(String email, String password);

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    User getUserByUserName(String username);

    void deleteUser(Long id);

    User getUserByEmail(String email);

    User getUserByResetToken(String token);

    User save(User user);

    UserResponse updateUser(Long id, UserRequest request);
}