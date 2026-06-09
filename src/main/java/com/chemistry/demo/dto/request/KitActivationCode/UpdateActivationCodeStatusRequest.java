package com.chemistry.demo.dto.request.KitActivationCode;

import com.chemistry.demo.enums.ActivationCodeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateActivationCodeStatusRequest {

    @NotNull
    private ActivationCodeStatus status;
}