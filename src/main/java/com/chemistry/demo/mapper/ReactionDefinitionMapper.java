package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.reaction.ReactionDefinitionResponse;
import com.chemistry.demo.entity.ReactionDefinition;

public class ReactionDefinitionMapper {

    private ReactionDefinitionMapper() {
    }

    public static ReactionDefinitionResponse toResponse(ReactionDefinition reaction) {
        if (reaction == null) {
            return null;
        }

        return ReactionDefinitionResponse.builder()
                .id(reaction.getId())
                .code(reaction.getCode())
                .name(reaction.getName())
                .equation(reaction.getEquation())
                .reactionType(reaction.getReactionType())
                .arSceneKey(reaction.getArSceneKey())
                .description(reaction.getDescription())
                .active(reaction.getActive())
                .build();
    }
}