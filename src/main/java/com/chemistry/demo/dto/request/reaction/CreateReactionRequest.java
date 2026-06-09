package com.chemistry.demo.dto.request.reaction;

import com.chemistry.demo.dto.request.reaction.ReactionSubstanceRequest;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReactionRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String equation;

    @NotNull
    private ReactionType reactionType;

    @NotNull
    private ArSceneKey arSceneKey;

    private String description;

    @NotNull
    @NotEmpty
    @Valid
    private List<ReactionSubstanceRequest> reactants;

    @NotNull
    @NotEmpty
    @Valid
    private List<ReactionSubstanceRequest> products;

    private Boolean active;
}