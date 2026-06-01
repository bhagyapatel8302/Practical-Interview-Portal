package com.tatvasoft.interview_portal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_solutions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "language", length = 50, nullable = false)
    private String language;

    @Column(name = "solution_code", columnDefinition = "TEXT")
    private String solutionCode;
}