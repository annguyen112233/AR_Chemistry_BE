package com.chemistry.demo.dto.response.quiz;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffQuizQuestionResponse {
    private String id;
    private Integer questionOrder;
    private String type;
    private String questionText;
    private String optionsJson;
    private String correctAnswer;
    private String explanation;
    private String difficulty;
    private String status;
}
