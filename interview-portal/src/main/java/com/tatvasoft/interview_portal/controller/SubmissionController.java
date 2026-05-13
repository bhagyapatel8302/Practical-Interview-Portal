package com.tatvasoft.interview_portal.controller;

import com.tatvasoft.interview_portal.dto.SubmissionRequest;
import com.tatvasoft.interview_portal.dto.SubmissionResponse;
import com.tatvasoft.interview_portal.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService service;

    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @PostMapping
    public SubmissionResponse create(@RequestBody SubmissionRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<SubmissionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SubmissionResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public SubmissionResponse update(@PathVariable Long id,
                                     @RequestBody SubmissionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted successfully";
    }
}