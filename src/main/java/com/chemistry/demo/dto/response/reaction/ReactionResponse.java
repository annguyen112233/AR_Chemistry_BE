package com.chemistry.demo.dto.response.reaction;

import com.chemistry.demo.dto.response.reaction.ReactionSubstanceResponse;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReactionResponse {

    private String id;
    private String code;
    private String name;
    private String equation;

    private ReactionCategory reactionCategory;
    private ReactionType reactionType;
    private ArSceneKey arSceneKey;

    private String description;
    private String script;

    private Integer grade;
    private Boolean active;

    private List<ReactionSubstanceResponse> reactants;
    private List<ReactionSubstanceResponse> products;
}