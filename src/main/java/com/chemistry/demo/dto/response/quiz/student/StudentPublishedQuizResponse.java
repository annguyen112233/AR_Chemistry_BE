package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentPublishedQuizResponse {
    private String lessonCode;
    private String lessonTitle;
    private String chapter;

    private String quizCode;
    private String quizTitle;
    private Integer version;
    private Long questionCount;
}
