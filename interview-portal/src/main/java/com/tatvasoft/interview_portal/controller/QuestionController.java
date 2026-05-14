package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.dto.ApiResponse;
import com.tatvasoft.interview_portal.dto.QuestionRequest;
import com.tatvasoft.interview_portal.dto.QuestionResponse;
import com.tatvasoft.interview_portal.entity.Question;
import com.tatvasoft.interview_portal.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Question>> addQuestion(
            @RequestBody QuestionRequest request) {

        Question question = questionService.addQuestion(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        question
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAll() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        questionService.getAllQuestions()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> get(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        questionService.getQuestion(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> update(
            @PathVariable Long id,
            @RequestBody QuestionRequest request) {

        Question question =
                questionService.updateQuestion(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        question
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        questionService.deleteQuestion(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        "Question deleted successfully"
                )
        );
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<Question>>> upload(
            @RequestParam("file") MultipartFile file) {

        List<Question> uploadedQuestions =
                questionService.uploadZip(file);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        null,
                        uploadedQuestions
                )
        );
    }
}