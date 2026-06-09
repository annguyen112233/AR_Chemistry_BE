package com.chemistry.demo.dto.response.lesson;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonImportResponse {
    private int total;
    private int created;
    private int updated;
    private int skipped;
}
