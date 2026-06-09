package com.chemistry.demo.controller.admin;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.dto.response.lesson.LessonImportResponse;
import com.chemistry.demo.dto.response.lesson.LessonResponse;
import com.chemistry.demo.services.lesson.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminImportLessonController {

    private final LessonService lessonService;

    @PostMapping(
            value = "/import-lessons",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<?> importLessons(@RequestParam("file") MultipartFile file) {
        LessonImportResponse result = lessonService.importLessons(file);
        return ApiResponse.ok()
                .data(result)
                .build();
    }

    @GetMapping("/lessons")
    public ApiResponse<PageResponse<LessonResponse>> getLessons(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC

            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<LessonResponse>>ok()
                .data(lessonService.getLessonsForStaff(pageable))
                .build();
    }


}
