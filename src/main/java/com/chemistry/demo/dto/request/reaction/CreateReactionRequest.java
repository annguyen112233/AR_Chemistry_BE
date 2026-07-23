package com.chemistry.demo.dto.request.reaction;

import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReactionRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String equation;

    @NotNull
    private ReactionCategory reactionCategory;

    @NotNull
    private ReactionType reactionType;

    @NotNull
    private ArSceneKey arSceneKey;

    @Size(max = 1000)
    private String description;

    private String script;

    @NotNull
    @Min(8)
    @Max(12)
    private Integer grade;

    private Boolean active;

    @NotEmpty
    @Size(min = 1, max = 2)
    @Valid
    private List<ReactionSubstanceRequest> reactants;

    @NotNull
    @Valid
    private List<ReactionSubstanceRequest> products;
}