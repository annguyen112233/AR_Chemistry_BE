package com.chemistry.demo.dto.response.quiz.staff;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizImportResponse {
    private String jobCode;
    private String status;
}