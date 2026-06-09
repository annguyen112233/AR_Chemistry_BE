package com.chemistry.demo.dto.request.kit;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKitRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    private BigDecimal price;

    private Boolean active;

    /**
     * Nếu muốn tạo kit và chỉ định chất ngay.
     * Có thể truyền formula: ["Zn", "HCl", "NaOH"]
     */
    private List<String> substanceFormulas;
}