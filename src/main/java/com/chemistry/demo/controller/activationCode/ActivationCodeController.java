package com.chemistry.demo.controller.activationCode;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.KitActivationCode.GenerateActivationCodesRequest;
import com.chemistry.demo.dto.request.KitActivationCode.UpdateActivationCodeStatusRequest;
import com.chemistry.demo.dto.response.KitActivationCode.ActivationCodeResponse;
import com.chemistry.demo.enums.ActivationCodeStatus;
import com.chemistry.demo.services.KitActivationCode.ActivationCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activation-codes")
@RequiredArgsConstructor
public class ActivationCodeController {

    private final ActivationCodeService activationCodeService;

    @PostMapping("/generate")
    public ApiResponse<List<ActivationCodeResponse>> generateCodes(
            @Valid @RequestBody GenerateActivationCodesRequest request
    ) {
        List<ActivationCodeResponse> response =
                activationCodeService.generateCodes(request);

        return ApiResponse.<List<ActivationCodeResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ActivationCodeResponse>> getActivationCodes(
            @RequestParam(required = false) String kitId,
            @RequestParam(required = false) ActivationCodeStatus status,
            @RequestParam(required = false) String usedByUserId,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<ActivationCodeResponse> response =
                activationCodeService.getActivationCodes(
                        kitId,
                        status,
                        usedByUserId,
                        pageable
                );

        return ApiResponse.<PageResponse<ActivationCodeResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping("/code/{code}")
    public ApiResponse<ActivationCodeResponse> getByCode(
            @PathVariable String code
    ) {
        ActivationCodeResponse response = activationCodeService.getByCode(code);

        return ApiResponse.<ActivationCodeResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ActivationCodeResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateActivationCodeStatusRequest request
    ) {
        ActivationCodeResponse response =
                activationCodeService.updateStatus(id, request);

        return ApiResponse.<ActivationCodeResponse>ok()
                .data(response)
                .build();
    }
}
