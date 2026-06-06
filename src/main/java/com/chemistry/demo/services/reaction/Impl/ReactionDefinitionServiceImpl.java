package com.chemistry.demo.services.reaction.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.reaction.ReactionDefinitionResponse;
import com.chemistry.demo.dto.response.reaction.ReactionSummaryResponse;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.mapper.ReactionDefinitionMapper;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.services.reaction.ReactionDefinitionService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionDefinitionServiceImpl implements ReactionDefinitionService {
    private final ReactionDefinitionRepository reactionDefinitionRepository;

    @Override
    @Transactional(readOnly = true)
    public ReactionSummaryResponse getReactionSummary() {
        long totalReactions = reactionDefinitionRepository.countByActiveTrue();

        return ReactionSummaryResponse.builder()
                .totalReactions(totalReactions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReactionDefinitionResponse> getActiveReactions(Pageable pageable) {
        Page<ReactionDefinition> page =
                reactionDefinitionRepository.findByActiveTrue(pageable);

        return PageResponseUtils.toPageResponse(
                page,
                ReactionDefinitionMapper::toResponse
        );
    }
}
