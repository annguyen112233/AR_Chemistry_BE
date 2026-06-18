package com.chemistry.demo.controller.feedback;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.feedback.FeedbackRequest;
import com.chemistry.demo.dto.request.feedback.HandleFeedbackRequest;
import com.chemistry.demo.dto.response.feedback.FeedbackResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.services.feedback.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ApiResponse<String> submitFeedback(@RequestBody FeedbackRequest request) {
        String response = feedbackService.sendFeedback(request);
        return ApiResponse.<String>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<FeedbackListResponse>> getFeedbacksForAdmin(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC

            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<FeedbackListResponse>>ok()
                .data(feedbackService.getFeedbacksForStaff(pageable))
                .build();
    }

    @GetMapping("/details")
    public ApiResponse<FeedbackResponse> getFeedbackForAdmin(
            @RequestParam String feedbackId
    ) {
        return ApiResponse.<FeedbackResponse>ok()
                .data(feedbackService.getFeedbackForStaff(feedbackId))
                .build();
    }

    @PutMapping("/handle")
    public ApiResponse<FeedbackResponse> handleFeedback(
            @RequestParam String feedbackId,
            @RequestBody HandleFeedbackRequest request
    ) {
        return ApiResponse.<FeedbackResponse>ok()
                .data(feedbackService.handleFeedback(feedbackId, request))
                .build();
    }



    @GetMapping("/my-feedbacks/{feedbackId}")
    public ApiResponse<FeedbackResponse> getUserFeedback(
            @PathVariable String feedbackId
    ) {
        return ApiResponse.<FeedbackResponse>ok()
                .data(feedbackService.getUserFeedback(feedbackId))
                .build();
    }
}
