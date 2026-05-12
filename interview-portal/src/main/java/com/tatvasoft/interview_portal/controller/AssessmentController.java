package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.dto.AssessmentRequest;
import com.tatvasoft.interview_portal.dto.AssessmentResponse;
import com.tatvasoft.interview_portal.service.AssessmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService service;

    public AssessmentController(AssessmentService service) {
        this.service = service;
    }

    @PostMapping
    public AssessmentResponse create(@RequestBody AssessmentRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List <AssessmentResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AssessmentResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AssessmentResponse update(@PathVariable Long id,
                                     @RequestBody AssessmentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Assessment deactivated";
    }
}
