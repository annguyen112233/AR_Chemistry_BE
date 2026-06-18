package com.chemistry.demo.dto.response.KitActivationCode;

import com.chemistry.demo.dto.response.substance.UnlockedSubstanceResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivateKitResponse {

    private Boolean success;

    private String message;

    private String kitId;

    private String kitCode;

    private String kitName;

    private String activationCode;

    private List<UnlockedSubstanceResponse> unlockedSubstances;
}
