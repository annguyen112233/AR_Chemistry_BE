package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface StaffQuizAttemptService {
    PageResponse<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptResponse> getQuizAttempts(Pageable pageable);

    com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse getQuizAttemptDetail(String attemptCode);
}
