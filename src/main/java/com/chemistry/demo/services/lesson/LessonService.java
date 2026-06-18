package com.chemistry.demo.services.lesson;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.dto.response.lesson.LessonImportResponse;
import com.chemistry.demo.dto.response.lesson.LessonResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface LessonService {
    LessonImportResponse importLessons(MultipartFile file);

    PageResponse<LessonResponse> getLessonsForStaff(Pageable pageable);
    String getLessonDetails(String lessonCode);
}
