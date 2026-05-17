package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.feedback.FeedBackResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.entity.Feedback;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    @Mapping(target = "displayName", expression = "java(resolveDisplayName(feedback))")
    FeedbackListResponse toFeedbackListResponse(Feedback feedback);

    @Mapping(target = "displayName", expression = "java(resolveDisplayName(feedback))")
    FeedBackResponse toFeedBackResponse(Feedback feedback);

    default String resolveDisplayName(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        if (Boolean.TRUE.equals(feedback.getAnonymous())) {
            return "Anonymous";
        }

        if (feedback.getUser() == null) {
            return null;
        }

        return feedback.getUser().getFullName();
    }
}
