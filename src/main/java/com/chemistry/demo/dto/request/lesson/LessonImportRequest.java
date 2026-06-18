package com.chemistry.demo.dto.request.lesson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonImportRequest {

    @JsonProperty("lesson_id")
    private String lessonId;

    @JsonProperty("lesson_number")
    private Integer lessonNumber;

    private String book;
    private Integer grade;
    private String subject;
    private String title;
    private String chapter;
    private String source;

    @JsonProperty("page_start")
    private Integer pageStart;

    @JsonProperty("page_end")
    private Integer pageEnd;

    private String content;
    private String status;

    @JsonProperty("ocr_quality")
    private String ocrQuality;
}
