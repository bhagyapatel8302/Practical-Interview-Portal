package com.tatvasoft.interview_portal.service;

import com.tatvasoft.interview_portal.dto.UserRequest;
import com.tatvasoft.interview_portal.dto.UserResponse;
import com.tatvasoft.interview_portal.entity.User;

import java.util.List;

public interface UserService {
    User login(String username, String password);

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    void deleteUser(Long id);

}