package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.feedback.FeedBackResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackListResponse toFeedbackListResponse(Feedback feedback);

    FeedBackResponse toFeedBackResponse(Feedback feedback);
}
