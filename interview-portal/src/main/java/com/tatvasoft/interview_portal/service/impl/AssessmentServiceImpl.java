package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.dto.AssessmentRequest;
import com.tatvasoft.interview_portal.dto.AssessmentResponse;
import com.tatvasoft.interview_portal.entity.Assessment;
import com.tatvasoft.interview_portal.enums.AssessmentStatus;
import com.tatvasoft.interview_portal.repository.AssessmentRepository;
import com.tatvasoft.interview_portal.service.AssessmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository repository;

    public AssessmentServiceImpl(AssessmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssessmentResponse create(AssessmentRequest request) {

        Assessment a = new Assessment();

        a.setCandidateId(request.getCandidateId());
        a.setTimeLimitMinutes(request.getTimeLimitMinutes());
        a.setStatus(AssessmentStatus.PENDING.toString());
        a.setIsActive(true);

        a.setStartedAt(request.getStartedAt());
        a.setCompletedAt(request.getCompletedAt());

        a.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : 1L);
        a.setCreatedAt(LocalDateTime.now());

        Assessment saved = repository.save(a);

        return map(saved);
    }

    @Override
    public List<AssessmentResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public AssessmentResponse getById(Long id) {
        Assessment a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        return map(a);
    }

    @Override
    public AssessmentResponse update(Long id, AssessmentRequest request) {

        Assessment a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        a.setCandidateId(request.getCandidateId());
        a.setTimeLimitMinutes(request.getTimeLimitMinutes());
        a.setStatus(request.getStatus());

        a.setStartedAt(request.getStartedAt());
        a.setCompletedAt(request.getCompletedAt());

        a.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : 1L);
        a.setUpdatedAt(LocalDateTime.now());

        return map(repository.save(a));
    }

    @Override
    public void delete(Long id) {

        Assessment a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Soft delete
        a.setIsActive(false);
        a.setUpdatedAt(LocalDateTime.now());
        a.setUpdatedBy(1L);

        repository.save(a);
    }

    private AssessmentResponse map(Assessment a) {
        return new AssessmentResponse(
                a.getId(),
                a.getCandidateId(),
                a.getStatus(),
                a.getTimeLimitMinutes(),
                a.getIsActive(),
                a.getStartedAt(),
                a.getCompletedAt(),
                a.getCreatedAt(),
                a.getCreatedBy(),
                a.getUpdatedAt(),
                a.getUpdatedBy()
        );
    }
}
