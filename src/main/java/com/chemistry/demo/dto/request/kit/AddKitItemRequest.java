package com.chemistry.demo.dto.request.kit;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddKitItemRequest {

    @NotBlank
    private String formula;

    private Integer quantity;
}
