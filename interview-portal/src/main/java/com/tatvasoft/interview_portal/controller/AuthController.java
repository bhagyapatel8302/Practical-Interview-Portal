package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.dto.*;
import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.service.EmailService;
import com.tatvasoft.interview_portal.service.UserService;
import com.tatvasoft.interview_portal.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, UserService userService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {

        try {
            User user = userService.login(request.getEmail(), request.getPassword());

            // check active user
            if (!Boolean.TRUE.equals(user.getIsActive())) {

                ApiResponse<LoginResponse> response = new ApiResponse<>(
                        401,
                        false,
                        List.of("User account is inactive"),
                        null
                );

                return ResponseEntity.status(401).body(response);
            }

            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken, user);

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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        User user = userService.getUserByEmail(request.getEmail());

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);

        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        userService.save(user);

        String resetLink =
                "http://localhost:4200/auth/reset-password/" + token;

        emailService.sendResetPasswordEmail(
                user.getEmail(),
                resetLink
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        "Reset password link sent on email"
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        User user =
                userService.getUserByResetToken(request.getToken());

        // check token expiry
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        // clear token after use
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userService.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        "Password reset successfully"
                )
        );
    }

    @GetMapping("/validate-reset-token/{token}")
    public ResponseEntity<ApiResponse<String>> validateResetToken(
            @PathVariable String token) {

        User user = userService.getUserByResetToken(token);

        // token not found
        if (user == null) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            400,
                            false,
                            List.of("Invalid reset token"),
                            null
                    )
            );
        }

        // token expired
        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            400,
                            false,
                            List.of("Reset token expired"),
                            null
                    )
            );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        "Token is valid"
                )
        );
    }
}
