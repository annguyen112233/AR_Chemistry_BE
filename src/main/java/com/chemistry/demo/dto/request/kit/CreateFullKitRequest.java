package com.chemistry.demo.dto.request.kit;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFullKitRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    private BigDecimal price;
}
