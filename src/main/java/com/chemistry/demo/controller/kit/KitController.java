package com.chemistry.demo.controller.kit;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.kit.AddKitItemRequest;
import com.chemistry.demo.dto.request.kit.CreateFullKitRequest;
import com.chemistry.demo.dto.request.kit.CreateKitRequest;
import com.chemistry.demo.dto.request.kit.UpdateKitRequest;
import com.chemistry.demo.dto.request.subtance.UpdateActiveRequest;
import com.chemistry.demo.dto.response.kit.KitResponse;
import com.chemistry.demo.services.kit.KitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kits")
@RequiredArgsConstructor
public class KitController {
    private final KitService kitService;

    @PostMapping
    public ApiResponse<KitResponse> createKit(
            @Valid @RequestBody CreateKitRequest request
    ) {
        KitResponse response = kitService.createKit(request);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @PostMapping("/full-kit")
    public ApiResponse<KitResponse> createFullKitFromIncludedSubstances(
            @Valid @RequestBody CreateFullKitRequest request
    ) {
        KitResponse response = kitService.createFullKitFromIncludedSubstances(request);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<KitResponse>> getKits(
            @RequestParam(required = false) Boolean active,

            @PageableDefault(
                    size = 20,
                    sort = "code",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<KitResponse> response = kitService.getKits(active, pageable);

        return ApiResponse.<PageResponse<KitResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<KitResponse> getKitById(
            @PathVariable String id
    ) {
        KitResponse response = kitService.getKitById(id);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/code/{code}")
    public ApiResponse<KitResponse> getKitByCode(
            @PathVariable String code
    ) {
        KitResponse response = kitService.getKitByCode(code);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<KitResponse> updateKit(
            @PathVariable String id,
            @Valid @RequestBody UpdateKitRequest request
    ) {
        KitResponse response = kitService.updateKit(id, request);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @PostMapping("/{kitId}/items")
    public ApiResponse<KitResponse> addItemToKit(
            @PathVariable String kitId,
            @Valid @RequestBody AddKitItemRequest request
    ) {
        KitResponse response = kitService.addItemToKit(kitId, request);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @DeleteMapping("/{kitId}/items/{substanceId}")
    public ApiResponse<KitResponse> removeItemFromKit(
            @PathVariable String kitId,
            @PathVariable String substanceId
    ) {
        KitResponse response = kitService.removeItemFromKit(kitId, substanceId);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/active")
    public ApiResponse<KitResponse> updateActiveStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateActiveRequest request
    ) {
        KitResponse response = kitService.updateActiveStatus(
                id,
                request.getActive()
        );

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{kitId}/items/{substanceId}/restore")
    public ApiResponse<KitResponse> restoreItemToKit(
            @PathVariable String kitId,
            @PathVariable String substanceId
    ) {
        KitResponse response = kitService.restoreItemToKit(kitId, substanceId);

        return ApiResponse.<KitResponse>ok()
                .data(response)
                .build();
    }
}
