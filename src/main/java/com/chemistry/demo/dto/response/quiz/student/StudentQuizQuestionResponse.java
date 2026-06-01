package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizQuestionResponse {
    private String id;
    private Integer questionOrder;
    private String type;
    private String questionText;
    private String optionsJson;
    private String difficulty;
}
