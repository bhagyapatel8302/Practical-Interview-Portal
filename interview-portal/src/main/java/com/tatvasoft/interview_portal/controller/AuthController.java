package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.dto.ApiResponse;
import com.tatvasoft.interview_portal.dto.LoginRequest;
import com.tatvasoft.interview_portal.dto.LoginResponse;
import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.service.UserService;
import com.tatvasoft.interview_portal.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {

        try {
            User user = userService.login(request.getEmail(), request.getPassword());

            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);

            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    200,
                    true,
                    null,
                    loginResponse
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    401,
                    false,
                    List.of(ex.getMessage()),
                    null
            );

            return ResponseEntity.status(401).body(response);
        }
    }
//    @PostMapping("/login")
//    public String login(@RequestParam String username,
//                        @RequestParam String password) {
//
//        if ("admin".equals(username) && "admin123".equals(password)) {
//
//            String accessToken = jwtUtil.generateAccessToken(username);
//            String refreshToken = jwtUtil.generateRefreshToken(username);
//
//            return Map.of(
//                    "accessToken", accessToken,
//                    "refreshToken", refreshToken
//            ).toString();
//        }
//
//        throw new RuntimeException("Invalid credentials");
//    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestParam String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String type = jwtUtil.extractType(refreshToken);

        if (!"refresh".equals(type)) {
            throw new RuntimeException("Not a refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);

        User user = userService.getUserByUserName(username);

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return Map.of("accessToken", newAccessToken);
    }
}
