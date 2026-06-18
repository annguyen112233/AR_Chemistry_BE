package com.chemistry.demo.dto.request.chemical;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChemicalCardRequest {

    @NotBlank
    private String cardCode;

    @NotBlank
    private String qrPayload;

    /**
     * Map card tới substance bằng formula.
     * Ví dụ: Zn, HCl, NaOH
     */
    @NotBlank
    private String formula;

    private String displayName;

    private String imageUrl;

    private Boolean active;
}