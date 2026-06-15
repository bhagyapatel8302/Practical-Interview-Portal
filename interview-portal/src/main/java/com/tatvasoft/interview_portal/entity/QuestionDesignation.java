package com.tatvasoft.interview_portal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_designations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDesignation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}
