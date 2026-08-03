package com.chemistry.demo.dto.quizCSV.staff;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffQuizAttemptResponse {

    private String attemptCode;

    private String studentId;
    private String studentName;
    private String studentEmail;

    private String quizCode;
    private String quizTitle;

    private String lessonCode;
    private String lessonTitle;

    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;

    private String status;
    private Instant submittedAt;
}