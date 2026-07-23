package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizResponse {
    private String attemptCode;
    private Integer score;
    private Integer total;
    private Integer correctCount;
    private Instant submittedAt;
    private List<QuestionResult> results;

    @Getter
    @Builder
    public static class QuestionResult {
        private String questionId;
        private Boolean correct;
        private String studentAnswer;
        private String correctAnswer;
        private String explanation;
    }
}
