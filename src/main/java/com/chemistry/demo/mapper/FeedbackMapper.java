package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.feedback.FeedbackResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "displayName", expression = "java(feedback.getAnonymous() ? \"Anonymous\" : (feedback.getUser() != null ? feedback.getUser().getFullName() : \"Unknown\"))")
    FeedbackListResponse toFeedbackListResponse(Feedback feedback);

    @Mapping(target = "displayName", expression = "java(feedback.getAnonymous() ? \"Anonymous\" : (feedback.getUser() != null ? feedback.getUser().getFullName() : \"Unknown\"))")
    FeedbackResponse toFeedbackResponse(Feedback feedback);
}
