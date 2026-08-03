package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "lesson_code", unique = true, nullable = false)
    private String lessonCode; // bai_2_chat

    @Column(name = "lesson_number")
    private Integer lessonNumber;

    @Column(name = "book")
    private String book; // hoa_hoc_8_cu

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "subject")
    private String subject; // chemistry

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "chapter")
    private String chapter;

    @Column(name = "source")
    private String source;

    @Column(name = "page_start")
    private Integer pageStart;

    @Column(name = "page_end")
    private Integer pageEnd;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "status")
    private String status; // active / inactive

    @Column(name = "ocr_quality")
    private String ocrQuality; // needs_review / reviewed

    @Column(name = "clean_content", columnDefinition = "TEXT")
    private String cleanContent;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
}
