package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.dto.AssessmentRequest;
import com.tatvasoft.interview_portal.dto.AssessmentResponse;
import com.tatvasoft.interview_portal.entity.Assessment;
import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.enums.AssessmentStatus;
import com.tatvasoft.interview_portal.repository.AssessmentRepository;
import com.tatvasoft.interview_portal.repository.UserRepository;
import com.tatvasoft.interview_portal.service.AssessmentService;
import com.tatvasoft.interview_portal.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository repository;
    private final UserRepository userRepository;

    public AssessmentServiceImpl(AssessmentRepository repository,UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public AssessmentResponse create(AssessmentRequest request) {

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        Assessment a = new Assessment();

        a.setCandidateId(request.getCandidateId());
        a.setTimeLimitMinutes(request.getTimeLimitMinutes());
        a.setStatus(AssessmentStatus.PENDING.toString());
        a.setIsActive(true);

        a.setStartedAt(request.getStartedAt());
        a.setCompletedAt(request.getCompletedAt());

        a.setCreatedBy(currentUser.getId());
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

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        Assessment a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        a.setCandidateId(request.getCandidateId());
        a.setTimeLimitMinutes(request.getTimeLimitMinutes());
        a.setStatus(request.getStatus());

        a.setStartedAt(request.getStartedAt());
        a.setCompletedAt(request.getCompletedAt());

        a.setUpdatedBy(currentUser.getId());
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
                a.getTitle(),
                a.getStartedAt(),
                a.getCompletedAt(),
                a.getCreatedAt(),
                a.getCreatedBy(),
                a.getUpdatedAt(),
                a.getUpdatedBy()
        );
    }
}
