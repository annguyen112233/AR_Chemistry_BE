package com.chemistry.demo.controller.inventory;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.KitActivationCode.ActivateKitRequest;
import com.chemistry.demo.dto.response.KitActivationCode.ActivateKitResponse;
import com.chemistry.demo.dto.response.inventory.SyncInventoryResponse;
import com.chemistry.demo.dto.response.inventory.UserInventoryResponse;
import com.chemistry.demo.dto.response.substance.SubstanceDetailResponse;
import com.chemistry.demo.services.inventory.InventoryService;
import com.chemistry.demo.services.inventory.InventorySyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventorySyncService inventorySyncService;

    @PostMapping("/activate-kit")
    public ApiResponse<ActivateKitResponse> activateKit(
            @Valid @RequestBody ActivateKitRequest request
    ) {
        ActivateKitResponse response = inventoryService.activateKit(request);

        return ApiResponse.<ActivateKitResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<PageResponse<UserInventoryResponse>> getMyInventory(
            @PageableDefault(
                    size = 20,
                    sort = "acquiredAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<UserInventoryResponse> response =
                inventoryService.getMyInventory(pageable);

        return ApiResponse.<PageResponse<UserInventoryResponse>>ok()
                .data(response)
                .build();
    }

    @PostMapping("/{kitId}/sync-inventory")
    public ApiResponse<SyncInventoryResponse> syncKitInventory(
            @PathVariable String kitId
    ) {
        SyncInventoryResponse response = inventorySyncService.syncKitInventory(kitId);

        return ApiResponse.<SyncInventoryResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/substances/{substanceId}/detail")
    public ApiResponse<SubstanceDetailResponse> getSubstanceDetail(
            @PathVariable String substanceId
    ) {
        SubstanceDetailResponse response =
                inventoryService.getSubstanceDetail(substanceId);

        return ApiResponse.<SubstanceDetailResponse>ok()
                .data(response)
                .build();
    }
}