package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.enums.QuizImportStatus;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizImportResponse {
    private String jobCode;
    private QuizImportStatus status;
}