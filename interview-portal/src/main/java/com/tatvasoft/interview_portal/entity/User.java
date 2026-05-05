package com.tatvasoft.interview_portal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}