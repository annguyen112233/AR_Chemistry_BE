package com.chemistry.demo.dto.response.reaction;

import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionDefinitionResponse {

    private String id;

    private String code;

    private String name;

    private String equation;

    private ReactionType reactionType;

    private ArSceneKey arSceneKey;

    private String description;

    private Boolean active;
}