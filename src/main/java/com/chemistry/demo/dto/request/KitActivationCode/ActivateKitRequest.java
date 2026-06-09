package com.chemistry.demo.dto.request.KitActivationCode;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivateKitRequest {

    @NotBlank
    private String activationCode;
}
