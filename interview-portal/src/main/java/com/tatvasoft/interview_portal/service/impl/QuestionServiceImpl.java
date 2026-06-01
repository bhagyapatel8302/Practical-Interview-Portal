package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.dto.*;
import com.tatvasoft.interview_portal.entity.Category;
import com.tatvasoft.interview_portal.entity.Question;
import com.tatvasoft.interview_portal.entity.QuestionSolution;
import com.tatvasoft.interview_portal.entity.User;
import com.tatvasoft.interview_portal.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(QuestionsRepository questionRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public QuestionResponse addQuestion(QuestionRequest request) {

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

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            // Fetch all categories matching the IDs from the database
            java.util.List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());

            // Convert the List to a Set (since your Question entity uses a Set for categories)
            q.setCategories(new java.util.HashSet<>(categories));
        }

        if (request.getSolutions() != null && !request.getSolutions().isEmpty()) {
            java.util.List<QuestionSolution> solutionEntities = request.getSolutions().stream().map(dto -> {
                QuestionSolution sol = new QuestionSolution();
                sol.setLanguage(dto.getLanguage());
                sol.setSolutionCode(dto.getSolutionCode());

                // 🔥 CRUCIAL: Set the parent question so the foreign key isn't null!
                sol.setQuestion(q);

                return sol;
            }).collect(java.util.stream.Collectors.toList());

            q.setSolutions(solutionEntities);
        }

        Question savedQuestion = questionRepository.save(q);

        return mapToQuestionResponse(savedQuestion);
    }

    @Override
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(u -> new CategoryResponse(
                        u.getId(),
                        u.getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        return mapToQuestionResponse(question);
    }

    @Override
    public QuestionResponse updateQuestion(Long id, QuestionRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        q.setTitle(request.getTitle());
        q.setDescription(request.getDescription());
        q.setDifficulty(request.getDifficulty());
        q.setEstimatedTime(request.getEstimatedTime());
        q.setIsActive(request.getIsActive());
        q.setUpdatedBy(currentUser.getId());

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            java.util.List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            q.setCategories(new java.util.HashSet<>(categories));
        } else {
            q.getCategories().clear();
        }

        if (request.getSolutions() != null && !request.getSolutions().isEmpty()) {

            java.util.List<QuestionSolution> newSolutions = request.getSolutions().stream().map(dto -> {
                QuestionSolution sol = new QuestionSolution();
                sol.setLanguage(dto.getLanguage());
                sol.setSolutionCode(dto.getSolutionCode());
                sol.setQuestion(q);
                return sol;
            }).collect(java.util.stream.Collectors.toList());

            q.getSolutions().clear();
            q.getSolutions().addAll(newSolutions);

        } else {
            q.getSolutions().clear();
        }

        Question updatedQuestion = questionRepository.save(q);

        return mapToQuestionResponse(updatedQuestion);
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

    private QuestionResponse mapToQuestionResponse(Question question) {
        QuestionResponse response = new QuestionResponse();

        response.setId(question.getId());
        response.setTitle(question.getTitle());
        response.setDescription(question.getDescription());
        response.setDifficulty(question.getDifficulty());
        response.setEstimatedTime(question.getEstimatedTime());
        response.setIsActive(question.getIsActive());

        if (question.getCategories() != null) {
            response.setCategories(question.getCategories().stream()
                    .map(cat -> new CategoryDto(cat.getId(), cat.getName()))
                    .collect(Collectors.toList()));
        }

        if (question.getSolutions() != null) {
            response.setSolutions(question.getSolutions().stream()
                    .map(sol -> new SolutionDto(sol.getLanguage(), sol.getSolutionCode()))
                    .collect(Collectors.toList()));
        }

        return response;
    }
}