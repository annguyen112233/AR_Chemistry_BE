package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.request.quiz.StartQuizImportRequest;
import com.chemistry.demo.dto.response.quiz.StartQuizImportResponse;
import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.entity.QuizImportJob;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.repository.QuizImportJobRepository;
import com.chemistry.demo.services.quiz.QuizImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.batch.core.JobParametersBuilder;

@Service
@RequiredArgsConstructor
public class QuizImportServiceImpl implements QuizImportService {

    private final LessonRepository lessonRepository;
    private final QuizImportJobRepository quizImportJobRepository;
    private final JobLauncher jobLauncher;
    private final Job importQuizJob;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public StartQuizImportResponse startImport(StartQuizImportRequest request) {
        validateRequest(request);

        Lesson lesson = lessonRepository.findByLessonCode(request.getLessonCode())
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + request.getLessonCode()));

        String jobCode = "quiz_import_" + System.currentTimeMillis();

        QuizImportJob job = new QuizImportJob();
        job.setJobCode(jobCode);
        job.setLesson(lesson);
        job.setOriginalFilename(request.getOriginalFilename());
        job.setS3Key(request.getS3Key());
        job.setStatus("UPLOADED");
        job.setTotalRows(0);
        job.setSuccessRows(0);
        job.setFailedRows(0);

        quizImportJobRepository.save(job);

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("jobCode", job.getJobCode())
                    .addString("lessonCode", lesson.getLessonCode())
                    .addString("s3Key", job.getS3Key())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importQuizJob, params);

        } catch (Exception e) {
            job.setStatus("FAILED");
            job.setErrorMessage("Cannot start batch job: " + e.getMessage());
            quizImportJobRepository.save(job);
            throw new RuntimeException("Cannot start quiz import batch job", e);
        }

        return new StartQuizImportResponse(job.getJobCode(), job.getStatus());
    }

    private void validateRequest(StartQuizImportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required");
        }

        if (request.getLessonCode() == null || request.getLessonCode().isBlank()) {
            throw new IllegalArgumentException("lessonCode is required");
        }

        if (request.getS3Key() == null || request.getS3Key().isBlank()) {
            throw new IllegalArgumentException("s3Key is required");
        }

        if (!request.getS3Key().startsWith("quiz-import/")) {
            throw new IllegalArgumentException("Invalid s3Key for quiz import");
        }
    }
}