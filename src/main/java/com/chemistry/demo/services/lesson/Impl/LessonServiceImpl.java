package com.chemistry.demo.services.lesson.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.lesson.LessonImportRequest;
import com.chemistry.demo.dto.response.lesson.LessonImportResponse;
import com.chemistry.demo.dto.response.lesson.LessonResponse;
import com.chemistry.demo.entity.Feedback;
import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.mapper.LessonMapper;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.services.lesson.LessonService;
import com.chemistry.demo.utils.PageResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ObjectMapper objectMapper;
    private final LessonMapper lessonMapper;
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LessonImportResponse importLessons(MultipartFile file) {
        try {
            List<LessonImportRequest> requests = objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<List<LessonImportRequest>>() {}
            );

            int created = 0;
            int updated = 0;
            int skipped = 0;

            for (LessonImportRequest request : requests) {
                if (request.getLessonId() == null || request.getTitle() == null) {
                    skipped++;
                    continue;
                }

                Lesson lesson = lessonRepository
                        .findByLessonCode(request.getLessonId())
                        .orElseGet(Lesson::new);

                boolean isNew = lesson.getId() == null;

                lesson.setLessonCode(request.getLessonId());
                lesson.setLessonNumber(request.getLessonNumber());
                lesson.setBook(request.getBook());
                lesson.setGrade(request.getGrade());
                lesson.setSubject(request.getSubject());
                lesson.setTitle(request.getTitle());
                lesson.setChapter(request.getChapter());
                lesson.setSource(request.getSource());
                lesson.setPageStart(request.getPageStart());
                lesson.setPageEnd(request.getPageEnd());
                lesson.setContent(request.getContent());
                lesson.setStatus(request.getStatus());
                lesson.setOcrQuality(request.getOcrQuality());

                lessonRepository.save(lesson);

                if (isNew) created++;
                else updated++;
            }

            return new LessonImportResponse(requests.size(), created, updated, skipped);

        } catch (Exception e) {
            throw new RuntimeException("Import lessons failed: " + e.getMessage(), e);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public PageResponse<LessonResponse> getLessonsForStaff(Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findAll(pageable);

        return PageResponseUtils.toPageResponse(
                lessons,
                lessonMapper::toLessonResponse
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public String getLessonDetails(String lessonCode) {
        return lessonRepository.findByLessonCode(lessonCode)
                .map(Lesson::getContent)
                .orElse(null);
    }
}
