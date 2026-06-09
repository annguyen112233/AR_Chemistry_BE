package com.chemistry.demo.services.inventory;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.KitActivationCode.ActivateKitRequest;
import com.chemistry.demo.dto.response.KitActivationCode.ActivateKitResponse;
import com.chemistry.demo.dto.response.inventory.UserInventoryResponse;
import com.chemistry.demo.dto.response.substance.SubstanceDetailResponse;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    ActivateKitResponse activateKit(
            ActivateKitRequest request
    );

    PageResponse<UserInventoryResponse> getMyInventory(
            Pageable pageable
    );

    SubstanceDetailResponse getSubstanceDetail(String substanceId);
}
