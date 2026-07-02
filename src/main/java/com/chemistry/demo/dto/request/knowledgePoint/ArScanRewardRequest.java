package com.chemistry.demo.dto.request.knowledgePoint;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArScanRewardRequest {

    @NotBlank
    private String referenceId;
}