package com.chemistry.demo.dto.seed;

import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeedReactionItem {

    private String code;

    private String name;

    private String equation;

    private ReactionType reactionType;

    private ReactionCategory reactionCategory;

    private ArSceneKey arSceneKey;

    private String description;

    private String script;

    private Integer grade;

    private Boolean active;

    private List<SeedReactionSubstanceItem> reactants;

    private List<SeedReactionSubstanceItem> products;
}