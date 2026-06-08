package com.tatvasoft.interview_portal.ai.service;

import com.tatvasoft.interview_portal.entity.ReferenceSolution;

public interface AiSolutionGenerationService {

    ReferenceSolution generateSolution(
            String title,
            String description
    );
}