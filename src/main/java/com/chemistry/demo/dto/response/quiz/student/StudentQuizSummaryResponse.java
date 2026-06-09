package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizSummaryResponse {
    private String quizCode;
    private String lessonCode;
    private String title;
    private Integer version;
    private Long questionCount;
}
