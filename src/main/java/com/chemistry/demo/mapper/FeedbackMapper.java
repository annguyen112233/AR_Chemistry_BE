package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackResponse;
import com.chemistry.demo.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "displayName", expression = "java(resolveDisplayName(feedback))")
    FeedbackListResponse toFeedbackListResponse(Feedback feedback);

    @Mapping(target = "displayName", expression = "java(resolveDisplayName(feedback))")
    FeedbackResponse toFeedbackResponse(Feedback feedback);

    default String resolveDisplayName(Feedback feedback) {
        if (Boolean.TRUE.equals(feedback.getAnonymous())) {
            return "Anonymous";
        }

        if (feedback.getUser() == null) {
            return "Unknown";
        }

        if (feedback.getUser().getFullName() != null
                && !feedback.getUser().getFullName().isBlank()) {
            return feedback.getUser().getFullName();
        }

        if (feedback.getUser().getEmail() != null
                && !feedback.getUser().getEmail().isBlank()) {
            return feedback.getUser().getEmail();
        }

        return "Unknown";
    }
}