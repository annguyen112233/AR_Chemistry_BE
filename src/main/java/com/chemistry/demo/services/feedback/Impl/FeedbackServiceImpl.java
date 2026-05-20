package com.chemistry.demo.services.feedback.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.feedback.FeedbackRequest;
import com.chemistry.demo.dto.request.feedback.HandleFeedbackRequest;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackResponse;
import com.chemistry.demo.entity.Feedback;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.FeedbackPriority;
import com.chemistry.demo.enums.FeedbackStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.AppErrorCode;
import com.chemistry.demo.mapper.FeedbackMapper;
import com.chemistry.demo.repository.FeedbackRepository;
import com.chemistry.demo.services.feedback.FeedbackService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final SecurityUtils securityUtils;
    private final FeedbackMapper feedbackMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_TEACHER')")
    public String sendFeedback(FeedbackRequest feedback) {
        User user = securityUtils.getCurrentUserCognitoSub();

        Feedback feedbackEntity = Feedback.builder()
                .user(user)
                .content(feedback.getContent())
                .title(feedback.getTitle())
                .anonymous(
                        feedback.getAnonymous() != null
                                ? feedback.getAnonymous()
                                : false)
                .imageUrl(feedback.getImageUrl())
                .type(feedback.getType())
                .status(FeedbackStatus.OPEN)
                .priority(FeedbackPriority.MEDIUM)
                .build();

        feedbackRepository.save(feedbackEntity);

        return "Feedback sent successfully";
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<FeedbackListResponse> getFeedbacksForAdmin(Pageable pageable) {
        Page<Feedback> feedbacks = feedbackRepository.findAll(pageable);

        List<FeedbackListResponse> items = feedbacks.getContent()
                .stream()
                .map(feedbackMapper::toFeedbackListResponse)
                .toList();

        return PageResponse.<FeedbackListResponse>builder()
                .items(items)
                .page(feedbacks.getNumber())
                .size(feedbacks.getSize())
                .totalItems(feedbacks.getTotalElements())
                .totalPages(feedbacks.getTotalPages())
                .first(feedbacks.isFirst())
                .last(feedbacks.isLast())
                .hasNext(feedbacks.hasNext())
                .hasPrevious(feedbacks.hasPrevious())
                .build();

    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public FeedbackResponse getFeedbackForAdmin(String feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .map(feedbackMapper::toFeedbackResponse)
                .orElseThrow(() -> new AppException(AppErrorCode.FEEDBACK_NOT_FOUND));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public FeedbackResponse handleFeedback(String id, HandleFeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new AppException(AppErrorCode.FEEDBACK_NOT_FOUND));

        updateIfNotNull(request.getStatus(), feedback::setStatus);
        updateIfNotNull(request.getPriority(), feedback::setPriority);
        updateIfNotNull(request.getAdminReply(), feedback::setAdminReply);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return feedbackMapper.toFeedbackResponse(savedFeedback);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
