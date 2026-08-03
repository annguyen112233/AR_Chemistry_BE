package com.chemistry.demo.entity;

import com.chemistry.demo.enums.QuizImportStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_import_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizImportJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "job_code", unique = true, nullable = false)
    private String jobCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionDefinition reaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "s3_key")
    private String s3Key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuizImportStatus status;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "success_rows")
    private Integer successRows;

    @Column(name = "failed_rows")
    private Integer failedRows;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_by")
    private String createdBy;
}
