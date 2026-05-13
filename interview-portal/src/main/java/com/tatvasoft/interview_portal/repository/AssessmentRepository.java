package com.tatvasoft.interview_portal.repository;

import com.tatvasoft.interview_portal.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}
