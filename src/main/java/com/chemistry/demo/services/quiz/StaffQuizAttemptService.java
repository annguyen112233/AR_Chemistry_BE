package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizAttemptDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizAttemptResponse;
import org.springframework.data.domain.Pageable;

public interface StaffQuizAttemptService {
    PageResponse<StaffQuizAttemptResponse> getQuizAttempts(Pageable pageable);
    StaffQuizAttemptDetailResponse getQuizAttemptDetail(String attemptCode);
}
