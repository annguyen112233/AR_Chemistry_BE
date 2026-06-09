package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizDetailResponse {
    private String quizCode;
    private String lessonCode;
    private String title;
    private Integer version;
    private List<StudentQuizQuestionResponse> questions;
}
