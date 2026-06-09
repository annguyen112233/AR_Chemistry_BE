package com.chemistry.demo.dto.request.chemical;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChemicalCardRequest {

    @NotBlank
    private String qrPayload;

    @NotBlank
    private String formula;

    private String displayName;

    private String imageUrl;

    private Boolean active;
}
