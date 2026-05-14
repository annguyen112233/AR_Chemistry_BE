package com.chemistry.demo.services.feedback;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.feedback.FeedBackRequest;

import com.chemistry.demo.dto.request.feedback.HandleFeedbackRequest;
import com.chemistry.demo.dto.response.feedback.FeedBackResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    String sendFeedback(FeedBackRequest feedback);

    PageResponse<FeedbackListResponse> getFeedbacksForAdmin(Pageable pageable);

    FeedBackResponse getFeedbackForAdmin(String feedbackId);



    FeedBackResponse handleFeedback(String feedbackId, HandleFeedbackRequest handleFeedbackRequest);

}
