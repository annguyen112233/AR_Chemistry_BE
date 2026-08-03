package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Column(name = "type", nullable = false)
    private String type;
    // multiple_choice / true_false / fill_blank


    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;


    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;
    // ["A", "B", "C", "D"]


    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;


    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "difficulty")
    private String difficulty; // easy / medium / hard

    @Column(name = "status")
    private String status; // active / inactive
}
