package com.chemistry.demo.dto.request.reaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionSubstanceRequest {
    @NotBlank
    private String formula;

    @NotNull
    @Min(1)
    private Integer coefficient;
}
