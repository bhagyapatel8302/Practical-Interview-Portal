package com.tatvasoft.interview_portal.repository;

import com.tatvasoft.interview_portal.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepository extends JpaRepository<Question, Long> {
}
