package com.tatvasoft.interview_portal.service;

import com.tatvasoft.interview_portal.dto.QuestionRequest;
import com.tatvasoft.interview_portal.dto.QuestionResponse;
import com.tatvasoft.interview_portal.entity.Question;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {

    Question addQuestion(QuestionRequest request);

    List<QuestionResponse> getAllQuestions();

    Question getQuestion(Long id);

    Question updateQuestion(Long id, QuestionRequest request);

    void deleteQuestion(Long id);

    List<Question> uploadZip(MultipartFile file);
}