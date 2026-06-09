package com.chemistry.demo.dto.seed;

import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedReactionItem {

    private String code;

    private String name;

    private String equation;

    private ReactionType reactionType;

    private ArSceneKey arSceneKey;

    private String description;

    private Boolean active;

    private List<SeedReactionSubstanceItem> reactants;

    private List<SeedReactionSubstanceItem> products;
}