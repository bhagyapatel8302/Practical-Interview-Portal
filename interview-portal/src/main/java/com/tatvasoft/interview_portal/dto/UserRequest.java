package com.tatvasoft.interview_portal.dto;

import com.tatvasoft.interview_portal.entity.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private Long roleId;
    private Boolean isActive;
}