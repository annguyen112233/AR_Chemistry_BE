package com.chemistry.demo.mapper;

import com.chemistry.demo.entity.Conversation;
import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.dto.response.ai.ConversationDetailResponse;
import com.chemistry.demo.dto.response.ai.ConversationMessageResponse;
import com.chemistry.demo.dto.response.ai.ConversationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toResponse(Conversation conversation);

    ConversationDetailResponse toDetailResponse(Conversation conversation);

    ConversationMessageResponse toMessageResponse(ConversationMessage message);
}
