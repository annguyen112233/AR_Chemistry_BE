package com.chemistry.demo.services.reaction;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.reaction.ReactionDefinitionResponse;
import com.chemistry.demo.dto.response.reaction.ReactionSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface ReactionDefinitionService {
    ReactionSummaryResponse getReactionSummary();

    PageResponse<ReactionDefinitionResponse> getActiveReactions(Pageable pageable);
}
