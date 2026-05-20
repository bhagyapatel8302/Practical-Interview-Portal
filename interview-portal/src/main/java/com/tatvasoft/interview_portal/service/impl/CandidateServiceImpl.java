package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.dto.CandidateRequest;
import com.tatvasoft.interview_portal.dto.CandidateResponse;
import com.tatvasoft.interview_portal.entity.Candidate;
import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.exception.ResourceNotFoundException;
import com.tatvasoft.interview_portal.repository.CandidateRepository;
import com.tatvasoft.interview_portal.repository.UserRepository;
import com.tatvasoft.interview_portal.service.CandidateService;
import com.tatvasoft.interview_portal.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository,UserRepository userRepository) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CandidateResponse create(CandidateRequest request) {

        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Candidate email already exists");
        }

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        Candidate candidate = new Candidate();

        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());
        candidate.setEmail(request.getEmail());
        candidate.setExperience(request.getExperience());
        candidate.setDesignation(request.getDesignation());
        candidate.setIsActive(request.getIsActive());

        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setCreatedBy(currentUser.getId());

        Candidate saved = candidateRepository.save(candidate);

        return map(saved);
    }

    @Override
    public List<CandidateResponse> getAll() {

        return candidateRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse getById(Long id) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Candidate not found"));

        return map(candidate);
    }

    @Override
    public CandidateResponse update(Long id, CandidateRequest request) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Candidate not found"));

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());
        candidate.setEmail(request.getEmail());
        candidate.setExperience(request.getExperience());
        candidate.setDesignation(request.getDesignation());
        candidate.setIsActive(request.getIsActive());
        candidate.setUpdatedAt(LocalDateTime.now());
        candidate.setUpdatedBy(currentUser.getId());

        Candidate updated = candidateRepository.save(candidate);

        return map(updated);
    }

    @Override
    public void delete(Long id) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Candidate not found"));

        candidateRepository.delete(candidate);
    }

    private CandidateResponse map(Candidate candidate) {

        return new CandidateResponse(
                candidate.getId(),
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getEmail(),
                candidate.getExperience(),
                candidate.getDesignation(),
                candidate.getIsActive()
        );
    }
}