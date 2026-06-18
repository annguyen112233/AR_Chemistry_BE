package com.chemistry.demo.services.KitActivationCode;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.KitActivationCode.GenerateActivationCodesRequest;
import com.chemistry.demo.dto.request.KitActivationCode.UpdateActivationCodeStatusRequest;
import com.chemistry.demo.dto.response.KitActivationCode.ActivationCodeResponse;
import com.chemistry.demo.enums.ActivationCodeStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivationCodeService {
    List<ActivationCodeResponse> generateCodes(GenerateActivationCodesRequest request);
    PageResponse<ActivationCodeResponse> getActivationCodes(
            String kitId,
            ActivationCodeStatus status,
            String usedByUserId,
            Pageable pageable
    );

    ActivationCodeResponse getByCode(String code);
    ActivationCodeResponse updateStatus(
            String id,
            UpdateActivationCodeStatusRequest request
    );
}
