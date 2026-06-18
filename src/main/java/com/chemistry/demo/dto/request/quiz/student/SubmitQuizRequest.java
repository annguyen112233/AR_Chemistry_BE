package com.chemistry.demo.dto.request.quiz.student;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizRequest {
    private List<AnswerRequest> answers;

    @Getter
    @Setter
    public static class AnswerRequest {
        private String questionId;
        private String answer;
    }
}
