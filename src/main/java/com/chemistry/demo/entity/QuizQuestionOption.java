package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "quiz_question_options",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_question_option_key",
                        columnNames = {
                                "question_id",
                                "option_key"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "question_id",
            nullable = false
    )
    private QuizQuestion question;

    @Column(
            name = "option_key",
            nullable = false,
            length = 10
    )
    private String optionKey;

    @Column(
            name = "option_text",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String optionText;

    @Column(
            name = "option_order",
            nullable = false
    )
    private Integer optionOrder;
}