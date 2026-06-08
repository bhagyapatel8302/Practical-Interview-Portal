package com.tatvasoft.interview_portal.ai.service.impl;

import com.tatvasoft.interview_portal.ai.dto.EvaluationResult;
import com.tatvasoft.interview_portal.ai.dto.FileSubmissionRequest;
import com.tatvasoft.interview_portal.ai.dto.MultiQuestionEvaluationResult;
import com.tatvasoft.interview_portal.ai.dto.QuestionEvaluationResult;
import com.tatvasoft.interview_portal.ai.service.MultiQuestionEvaluationService;
import com.tatvasoft.interview_portal.ai.service.router.EvaluationRouter;
import com.tatvasoft.interview_portal.repository.CandidateSolutionRepository;
import com.tatvasoft.interview_portal.repository.QuestionsRepository;
import com.tatvasoft.interview_portal.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class MultiQuestionEvaluationServiceImpl implements MultiQuestionEvaluationService {

    private final EvaluationRouter evaluationRouter;
    private final QuestionsRepository questionsRepository;
    private final SubmissionRepository submissionRepository;
    private final CandidateSolutionRepository candidateSolutionRepository;

    public MultiQuestionEvaluationServiceImpl(EvaluationRouter evaluationRouter, QuestionsRepository questionsRepository, SubmissionRepository submissionRepository, CandidateSolutionRepository candidateSolutionRepository) {
        this.evaluationRouter = evaluationRouter;
        this.questionsRepository = questionsRepository;
        this.submissionRepository = submissionRepository;
        this.candidateSolutionRepository = candidateSolutionRepository;
    }

    @Override
    public MultiQuestionEvaluationResult evaluateAll(List<FileSubmissionRequest> questions) {

        ExecutorService executor = Executors.newFixedThreadPool(questions.size());
        List<Future<QuestionEvaluationResult>> futures = new ArrayList<>();

        // Submit all questions in parallel to whichever AI is active
        for (int i = 0; i < questions.size(); i++) {
            final int index = i;
            final FileSubmissionRequest request = questions.get(i);

            futures.add(executor.submit(() -> {
                QuestionEvaluationResult qResult = new QuestionEvaluationResult();
                qResult.setQuestionNumber(index + 1);
                String questionTitle = questionsRepository.findById(request.getQuestionId()).map(q -> q.getDescription()).orElse("Unknown Question");
                qResult.setQuestionTopic(questionTitle);
                EvaluationResult evaluation = evaluationRouter.routeEvaluation(request);
                qResult.setQuestionId(request.getQuestionId());
                qResult.setScore(evaluation.getScore());
                qResult.setFeedback(evaluation.getFeedback());
                qResult.setTimeComplexity(evaluation.getTimeComplexity());
                qResult.setSpaceComplexity(evaluation.getSpaceComplexity());
                qResult.setMissedEdgeCases(evaluation.getMissedEdgeCases());
                qResult.setSecurityIssues(evaluation.getSecurityIssues());
                qResult.setOptimizedCode(evaluation.getOptimizedCode());
                return qResult;
            }));
        }

        executor.shutdown();

        // Collect results in original order
        List<QuestionEvaluationResult> results = new ArrayList<>();
        for (Future<QuestionEvaluationResult> future : futures) {
            try {
                results.add(future.get(120, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                results.add(buildErrorResult("Evaluation timed out.", 0));
            } catch (Exception e) {
                results.add(buildErrorResult("Evaluation failed: " + e.getMessage(), 0));
            }
        }

        double overallScore =
                results.stream()
                        .mapToInt(
                                QuestionEvaluationResult::getScore
                        )
                        .average()
                        .orElse(0);
        MultiQuestionEvaluationResult finalResult = new MultiQuestionEvaluationResult();
        finalResult.setTotalQuestions(results.size());
        finalResult.setOverallScore(overallScore);
        finalResult.setEvaluations(results);

        finalResult.setIsSuccess(true);

        return finalResult;
    }

    private QuestionEvaluationResult buildErrorResult(String message, int score) {
        QuestionEvaluationResult qr =
                new QuestionEvaluationResult();

        qr.setScore(score);
        qr.setFeedback(message);

        return qr;
    }
}