package com.chemistry.demo.services.feedback;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.feedback.FeedbackRequest;

import com.chemistry.demo.dto.request.feedback.HandleFeedbackRequest;
import com.chemistry.demo.dto.response.feedback.FeedbackResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    String sendFeedback(FeedbackRequest feedback);

    PageResponse<FeedbackListResponse> getFeedbacksForAdmin(Pageable pageable);

    FeedbackResponse getFeedbackForAdmin(String feedbackId);



    FeedbackResponse handleFeedback(String feedbackId, HandleFeedbackRequest handleFeedbackRequest);

}
