package com.chemistry.demo.services.reaction;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.reaction.CreateReactionRequest;
import com.chemistry.demo.dto.request.reaction.UpdateReactionRequest;
import com.chemistry.demo.dto.response.reaction.ReactionResponse;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionType;
import org.springframework.data.domain.Pageable;

public interface ReactionService {
    ReactionResponse createReaction(CreateReactionRequest request);
    PageResponse<ReactionResponse> getReactions(
            Boolean active,
            ReactionType reactionType,
            ArSceneKey arSceneKey,
            Pageable pageable
    );

    ReactionResponse getReactionById(String id);
    ReactionResponse getReactionByCode(String code);
    ReactionResponse updateReaction(String id, UpdateReactionRequest request);
    ReactionResponse updateActiveStatus(String id, Boolean active);
}
