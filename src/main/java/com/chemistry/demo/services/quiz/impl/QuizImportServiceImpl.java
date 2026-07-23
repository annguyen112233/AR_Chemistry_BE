package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.request.quiz.StartQuizImportRequest;
import com.chemistry.demo.dto.response.quiz.staff.StartQuizImportResponse;
import com.chemistry.demo.entity.QuizImportJob;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.enums.QuizImportStatus;
import com.chemistry.demo.repository.QuizImportJobRepository;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.services.quiz.QuizImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizImportServiceImpl
        implements QuizImportService {

    private static final String QUIZ_IMPORT_PREFIX =
            "quiz-imports/";

    private final ReactionDefinitionRepository
            reactionDefinitionRepository;

    private final QuizImportJobRepository
            quizImportJobRepository;

    private final JobLauncher jobLauncher;

    private final Job importQuizJob;

    @Override
    @Transactional
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    public StartQuizImportResponse startImport(
            StartQuizImportRequest request
    ) {
        validateRequest(request);

        String reactionCode =
                request.getReactionCode()
                        .trim()
                        .toUpperCase();

        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findByCode(reactionCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reaction not found: "
                                                + reactionCode
                                )
                        );

        String jobCode =
                "quiz_import_"
                        + reactionCode
                        + "_"
                        + System.currentTimeMillis();

        QuizImportJob importJobEntity =
                new QuizImportJob();

        importJobEntity.setJobCode(jobCode);
        importJobEntity.setReaction(reaction);
        importJobEntity.setOriginalFilename(
                normalizeFilename(
                        request.getOriginalFilename()
                )
        );
        importJobEntity.setS3Key(
                request.getS3Key().trim()
        );
        importJobEntity.setStatus(
                QuizImportStatus.UPLOADED
        );
        importJobEntity.setTotalRows(0);
        importJobEntity.setSuccessRows(0);
        importJobEntity.setFailedRows(0);
        importJobEntity.setErrorMessage(null);

        quizImportJobRepository.save(
                importJobEntity
        );

        try {
            JobParameters parameters =
                    new JobParametersBuilder()
                            .addString(
                                    "jobCode",
                                    importJobEntity.getJobCode()
                            )
                            .addString(
                                    "reactionCode",
                                    reaction.getCode()
                            )
                            .addString(
                                    "s3Key",
                                    importJobEntity.getS3Key()
                            )
                            .addLong(
                                    "timestamp",
                                    System.currentTimeMillis()
                            )
                            .toJobParameters();

            jobLauncher.run(
                    importQuizJob,
                    parameters
            );

        } catch (Exception exception) {
            importJobEntity.setStatus(
                    QuizImportStatus.FAILED
            );

            importJobEntity.setErrorMessage(
                    "Cannot start batch job: "
                            + exception.getMessage()
            );

            quizImportJobRepository.save(
                    importJobEntity
            );

            throw new RuntimeException(
                    "Cannot start quiz import batch job",
                    exception
            );
        }

        return new StartQuizImportResponse(
                importJobEntity.getJobCode(),
                importJobEntity.getStatus()
        );
    }

    private void validateRequest(
            StartQuizImportRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Request is required"
            );
        }

        if (request.getReactionCode() == null
                || request.getReactionCode().isBlank()) {
            throw new IllegalArgumentException(
                    "reactionCode is required"
            );
        }

        if (request.getS3Key() == null
                || request.getS3Key().isBlank()) {
            throw new IllegalArgumentException(
                    "s3Key is required"
            );
        }

        if (!request.getS3Key()
                .startsWith(QUIZ_IMPORT_PREFIX)) {
            throw new IllegalArgumentException(
                    "Invalid s3Key for quiz import"
            );
        }

        if (request.getOriginalFilename() == null
                || request.getOriginalFilename().isBlank()) {
            throw new IllegalArgumentException(
                    "originalFilename is required"
            );
        }

        if (!request.getOriginalFilename()
                .toLowerCase()
                .endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "Only CSV file is allowed"
            );
        }
    }

    private String normalizeFilename(
            String filename
    ) {
        String normalized =
                filename.trim();

        /*
         * Loại bỏ path do client gửi,
         * ví dụ C:\\fakepath\\quiz.csv.
         */
        normalized =
                normalized.replace("\\", "/");

        int lastSlash =
                normalized.lastIndexOf('/');

        if (lastSlash >= 0) {
            normalized =
                    normalized.substring(
                            lastSlash + 1
                    );
        }

        return normalized;
    }
}