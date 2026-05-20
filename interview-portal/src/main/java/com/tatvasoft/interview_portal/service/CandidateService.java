package com.tatvasoft.interview_portal.service;

import com.tatvasoft.interview_portal.dto.CandidateRequest;
import com.tatvasoft.interview_portal.dto.CandidateResponse;

import java.util.List;

public interface CandidateService {

    CandidateResponse create(CandidateRequest request);

    List<CandidateResponse> getAll();

    CandidateResponse getById(Long id);

    CandidateResponse update(Long id, CandidateRequest request);

    void delete(Long id);
}