package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.dto.PageResponse;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffQuizDetailResponse {
    private String quizCode;
    private String lessonCode;
    private String title;
    private String status;
    private String generatedBy;
    private Integer version;
    private PageResponse<StaffQuizQuestionResponse> questions;
}
