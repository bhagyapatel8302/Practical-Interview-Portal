package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.dto.QuestionRequest;
import com.tatvasoft.interview_portal.dto.QuestionResponse;
import com.tatvasoft.interview_portal.entity.Question;
import com.tatvasoft.interview_portal.service.QuestionService;
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
    public Question addQuestion(@RequestBody QuestionRequest request) {
        return questionService.addQuestion(request);
    }

    @GetMapping
    public List<QuestionResponse> getAll() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public Question get(@PathVariable Long id) {
        return questionService.getQuestion(id);
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable Long id,
                           @RequestBody QuestionRequest request) {
        return questionService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "Deleted successfully";
    }

    // ZIP Upload
    @PostMapping("/upload")
    public List<Question> upload(@RequestParam("file") MultipartFile file) {
        return questionService.uploadZip(file);
    }
}