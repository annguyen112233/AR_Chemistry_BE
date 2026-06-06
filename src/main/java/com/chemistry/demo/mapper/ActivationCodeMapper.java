package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.KitActivationCode.ActivationCodeResponse;
import com.chemistry.demo.entity.Kit;
import com.chemistry.demo.entity.KitActivationCode;

public class ActivationCodeMapper {

    private ActivationCodeMapper() {
    }

    public static ActivationCodeResponse toResponse(KitActivationCode activationCode) {
        if (activationCode == null) {
            return null;
        }

        Kit kit = activationCode.getKit();

        String usedByUserId = null;
        if (activationCode.getUsedByUser() != null) {
            usedByUserId = activationCode.getUsedByUser().getCognitoSub();
        }

        return ActivationCodeResponse.builder()
                .id(activationCode.getId())
                .code(activationCode.getCode())
                .kitId(kit != null ? kit.getId() : null)
                .kitCode(kit != null ? kit.getCode() : null)
                .kitName(kit != null ? kit.getName() : null)
                .status(activationCode.getStatus())
                .usedByUserId(usedByUserId)
                .usedAt(activationCode.getUsedAt())
                .expiresAt(activationCode.getExpiresAt())
                .active(activationCode.getActive())
                .note(activationCode.getNote())
                .build();
    }
}
