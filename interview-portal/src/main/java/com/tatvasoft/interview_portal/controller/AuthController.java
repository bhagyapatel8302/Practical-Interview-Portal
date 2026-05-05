package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.service.UserService;
import com.tatvasoft.interview_portal.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username,
                                     @RequestParam String password) {

        User user = userService.login(username, password);

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
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

        String newAccessToken = jwtUtil.generateAccessToken(username);

        return Map.of("accessToken", newAccessToken);
    }
}
