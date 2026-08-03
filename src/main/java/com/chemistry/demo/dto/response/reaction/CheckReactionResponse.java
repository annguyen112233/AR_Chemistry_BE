package com.chemistry.demo.dto.response.reaction;

import com.chemistry.demo.dto.response.knowledgePoint.ArScanRewardResponse;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckReactionResponse {

    private Boolean matched;

    private String reason;

    private String message;

    private String reactionId;

    private String reactionCode;

    private String equation;

    private ReactionType reactionType;

    private ArSceneKey arSceneKey;

    private List<String> missingSubstances;

    private List<ReactionSubstanceResponse> reactants;

    private List<ReactionSubstanceResponse> products;

    private ArScanRewardResponse reward;
    private List<String> affectedQrPayloads;

}