package com.tatvasoft.interview_portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    private String title;
    private String description;
    private String difficulty;
    private Integer estimatedTime;
    private Boolean isActive;
}