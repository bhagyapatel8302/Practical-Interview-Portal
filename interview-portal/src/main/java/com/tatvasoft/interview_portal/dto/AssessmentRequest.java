package com.tatvasoft.interview_portal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssessmentRequest {

    private Long candidateId;
    private String status;
    private Integer timeLimitMinutes;
    private Boolean isActive;
    private String title;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private Long createdBy;
    private Long updatedBy;
}