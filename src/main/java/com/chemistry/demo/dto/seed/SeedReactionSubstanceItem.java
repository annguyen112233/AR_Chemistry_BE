package com.chemistry.demo.dto.seed;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedReactionSubstanceItem {

    private String formula;

    private Integer coefficient;
}
