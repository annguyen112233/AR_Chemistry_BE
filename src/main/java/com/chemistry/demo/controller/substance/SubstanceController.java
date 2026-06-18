package com.chemistry.demo.controller.substance;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.subtance.CreateSubstanceRequest;
import com.chemistry.demo.dto.request.subtance.UpdateActiveRequest;
import com.chemistry.demo.dto.request.subtance.UpdateIncludedInFullKitRequest;
import com.chemistry.demo.dto.request.subtance.UpdateSubstanceRequest;
import com.chemistry.demo.dto.response.substance.SubstanceResponse;
import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import com.chemistry.demo.services.subtance.SubstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/substances")
@RequiredArgsConstructor
public class SubstanceController {

    private final SubstanceService substanceService;

    @PostMapping
    public ApiResponse<SubstanceResponse> createSubstance(
            @Valid @RequestBody CreateSubstanceRequest request
    ) {
        SubstanceResponse response = substanceService.createSubstance(request);

        return ApiResponse.<SubstanceResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<SubstanceResponse>> getSubstances(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) ChemicalGroup chemicalGroup,
            @RequestParam(required = false) ChemicalSubstanceType type,
            @RequestParam(required = false) Boolean includedInFullKit,

            @PageableDefault(
                    size = 20,
                    sort = "formula",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<SubstanceResponse> response = substanceService.getSubstances(
                active,
                chemicalGroup,
                type,
                includedInFullKit,
                pageable
        );

        return ApiResponse.<PageResponse<SubstanceResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SubstanceResponse> getSubstanceById(
            @PathVariable String id
    ) {
        SubstanceResponse response = substanceService.getSubstanceById(id);

        return ApiResponse.<SubstanceResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/formula/{formula}")
    public ApiResponse<SubstanceResponse> getSubstanceByFormula(
            @PathVariable String formula
    ) {
        SubstanceResponse response = substanceService.getSubstanceByFormula(formula);

        return ApiResponse.<SubstanceResponse>ok()
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<SubstanceResponse> updateSubstance(
            @PathVariable String id,
            @Valid @RequestBody UpdateSubstanceRequest request
    ) {
        SubstanceResponse response = substanceService.updateSubstance(id, request);

        return ApiResponse.<SubstanceResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/active")
    public ApiResponse<SubstanceResponse> updateActiveStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateActiveRequest request
    ) {
        SubstanceResponse response = substanceService.updateActiveStatus(
                id,
                request.getActive()
        );

        return ApiResponse.<SubstanceResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/included-in-full-kit")
    public ApiResponse<SubstanceResponse> updateIncludedInFullKit(
            @PathVariable String id,
            @Valid @RequestBody UpdateIncludedInFullKitRequest request
    ) {
        SubstanceResponse response = substanceService.updateIncludedInFullKit(
                id,
                request.getIncludedInFullKit()
        );

        return ApiResponse.<SubstanceResponse>ok()
                .data(response)
                .build();
    }


}
