package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.dto.QuestionRequest;
import com.tatvasoft.interview_portal.dto.QuestionResponse;
import com.tatvasoft.interview_portal.entity.Question;
import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.repository.QuestionsRepository;
import com.tatvasoft.interview_portal.repository.UserRepository;
import com.tatvasoft.interview_portal.service.QuestionService;
import com.tatvasoft.interview_portal.util.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionsRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(QuestionsRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Question addQuestion(QuestionRequest request) {

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        Question q = new Question();
        q.setTitle(request.getTitle());
        q.setDescription(request.getDescription());
        q.setDifficulty(request.getDifficulty());
        q.setEstimatedTime(request.getEstimatedTime());
        q.setIsActive(request.getIsActive());
        q.setCreatedBy(currentUser.getId());

        return questionRepository.save(q);
    }

    @Override
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(u -> new QuestionResponse(
                        u.getId(),
                        u.getTitle(),
                        u.getDescription(),
                        u.getDifficulty(),
                        u.getEstimatedTime(),
                        u.getIsActive()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    @Override
    public Question updateQuestion(Long id, QuestionRequest request) {

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        q.setTitle(request.getTitle());
        q.setDescription(request.getDescription());
        q.setDifficulty(request.getDifficulty());
        q.setEstimatedTime(request.getEstimatedTime());
        q.setIsActive(request.getIsActive());
        q.setUpdatedBy(currentUser.getId());

        return questionRepository.save(q);
    }

    @Override
    public void deleteQuestion(Long id) {

        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found");
        }

        questionRepository.deleteById(id);
    }

    @Override
    public List<Question> uploadZip(MultipartFile file) {

        String username = SecurityUtil.getCurrentUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged in user not found"));

        List<Question> uploaded = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (!entry.isDirectory() && entry.getName().endsWith(".txt")) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(zis));

                    String title = reader.readLine();
                    String difficulty = reader.readLine();
                    Integer time = Integer.parseInt(reader.readLine());
                    String description = reader.readLine();
                    Boolean status = Boolean.parseBoolean(reader.readLine());

                    Question q = new Question();

                    q.setTitle(title);
                    q.setDescription(description);
                    q.setDifficulty(difficulty);
                    q.setEstimatedTime(time);
                    q.setIsActive(status);

                    q.setCreatedBy(currentUser.getId());

                    questionRepository.save(q);
                    uploaded.add(q);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing ZIP file");
        }

        return uploaded;
    }
}