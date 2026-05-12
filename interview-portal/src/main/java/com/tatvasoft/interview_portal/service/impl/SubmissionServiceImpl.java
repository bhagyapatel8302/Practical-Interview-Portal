package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.dto.SubmissionRequest;
import com.tatvasoft.interview_portal.dto.SubmissionResponse;
import com.tatvasoft.interview_portal.entity.Submission;
import com.tatvasoft.interview_portal.repository.SubmissionRepository;
import com.tatvasoft.interview_portal.service.SubmissionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository repository;

    public SubmissionServiceImpl(SubmissionRepository repository) {
        this.repository = repository;
    }

    @Override
    public SubmissionResponse create(SubmissionRequest request) {

        Submission s = new Submission();

        s.setAssessmentId(request.getAssessmentId());
        s.setReferenceFileId(request.getReferenceFileId());
        s.setCandidateFileId(request.getCandidateFileId());

        s.setCode(request.getCode());
        s.setOutput(request.getOutput());

        s.setAiScore(request.getAiScore());
        s.setAiFeedback(request.getAiFeedback());

        s.setEvaluatedAt(request.getEvaluatedAt());

        s.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : 1L);
        s.setCreatedAt(LocalDateTime.now());

        return map(repository.save(s));
    }

    @Override
    public List<SubmissionResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public SubmissionResponse getById(Long id) {

        Submission s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        return map(s);
    }

    @Override
    public SubmissionResponse update(Long id, SubmissionRequest request) {

        Submission s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        s.setCode(request.getCode());
        s.setOutput(request.getOutput());

        s.setAiScore(request.getAiScore());
        s.setAiFeedback(request.getAiFeedback());

        s.setEvaluatedAt(request.getEvaluatedAt());

        s.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : 1L);
        s.setUpdatedAt(LocalDateTime.now());

        return map(repository.save(s));
    }

    @Override
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Submission not found");
        }

        repository.deleteById(id);
    }

    private SubmissionResponse map(Submission s) {
        return new SubmissionResponse(
                s.getId(),
                s.getAssessmentId(),
                s.getReferenceFileId(),
                s.getCandidateFileId(),
                s.getCode(),
                s.getOutput(),
                s.getAiScore(),
                s.getAiFeedback(),
                s.getEvaluatedAt(),
                s.getCreatedAt(),
                s.getCreatedBy(),
                s.getUpdatedAt(),
                s.getUpdatedBy()
        );
    }
}