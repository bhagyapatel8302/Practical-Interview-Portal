package com.tatvasoft.interview_portal.repository;

import com.tatvasoft.interview_portal.entity.ReferenceSolution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferenceSolutionRepository
        extends JpaRepository<ReferenceSolution, Long> {

    Optional<ReferenceSolution>
    findByQuestionIdAndIsActiveTrue(Long questionId);
}