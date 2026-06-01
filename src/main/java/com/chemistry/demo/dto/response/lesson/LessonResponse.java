package com.chemistry.demo.dto.response.lesson;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private String lessonCode;
    private Integer lessonNumber;
    private String title;
    private String chapter;
    private Integer pageStart;
    private Integer pageEnd;
    private String status;
}
