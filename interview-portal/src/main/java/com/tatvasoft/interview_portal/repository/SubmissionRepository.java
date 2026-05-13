package com.tatvasoft.interview_portal.repository;

import com.tatvasoft.interview_portal.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
