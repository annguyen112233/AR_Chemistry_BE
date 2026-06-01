package com.chemistry.demo.dto.response.quiz;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffQuizSummaryResponse {
    private String quizCode;
    private String title;
    private String status;
    private String generatedBy;
    private Integer version;
    private Long questionCount;
}
