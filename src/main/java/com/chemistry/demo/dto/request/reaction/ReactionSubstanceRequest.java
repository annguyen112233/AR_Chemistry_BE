package com.chemistry.demo.dto.request.reaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionSubstanceRequest {

    @NotBlank
    private String formula;

    @NotNull
    @Min(1)
    private Integer coefficient;

    /*
     * Có thể không cho frontend gửi order,
     * backend sẽ tự tính theo vị trí trong list.
     */
}