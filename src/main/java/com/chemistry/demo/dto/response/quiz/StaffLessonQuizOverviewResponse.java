package com.chemistry.demo.dto.response.quiz;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffLessonQuizOverviewResponse {
    private String lessonCode;
    private Integer lessonNumber;
    private String lessonTitle;
    private String chapter;

    private boolean hasQuiz;

    private String latestQuizCode;
    private String latestQuizTitle;
    private String latestQuizStatus;
    private Integer latestQuizVersion;

    private Long questionCount;
}
