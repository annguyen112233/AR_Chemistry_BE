package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "quiz_code", unique = true, nullable = false)
    private String quizCode; // quiz_bai_2_chat_v1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "generated_by")
    private String generatedBy; // ai / manual

    @Column(name = "status")
    private String status; // draft / reviewed / published / archived

    @Column(name = "version")
    private Integer version;
}