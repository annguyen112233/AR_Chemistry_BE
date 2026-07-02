package com.chemistry.demo.dto.request.KitActivationCode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateActivationCodesRequest {

    @NotBlank
    private String kitCode;

    @Min(1)
    @Max(1000)
    private Integer quantity;

    private Instant expiresAt;

    private String note;
}
