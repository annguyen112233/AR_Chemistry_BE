package com.chemistry.demo.controller.chemical;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.chemical.CreateChemicalCardRequest;
import com.chemistry.demo.dto.request.chemical.UpdateChemicalCardRequest;
import com.chemistry.demo.dto.request.subtance.UpdateActiveRequest;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.services.chemical.ChemicalCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chemical-cards")
@RequiredArgsConstructor
public class ChemicalCardController {

    private final ChemicalCardService chemicalCardService;

    @PostMapping
    public ApiResponse<ChemicalCardResponse> createCard(
            @Valid @RequestBody CreateChemicalCardRequest request
    ) {
        ChemicalCardResponse response = chemicalCardService.createCard(request);
        return ApiResponse.<ChemicalCardResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ChemicalCardResponse>> getCards(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String substanceId,

            @PageableDefault(
                    size = 20,
                    sort = "cardCode",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<ChemicalCardResponse> response =
                chemicalCardService.getCards(active, substanceId, pageable);

        return ApiResponse.<PageResponse<ChemicalCardResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ChemicalCardResponse> getById(
            @PathVariable String id
    ) {
        ChemicalCardResponse response = chemicalCardService.getById(id);

        return ApiResponse.<ChemicalCardResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/code/{cardCode}")
    public ApiResponse<ChemicalCardResponse> getByCardCode(
            @PathVariable String cardCode
    ) {
        ChemicalCardResponse response = chemicalCardService.getByCardCode(cardCode);

        return ApiResponse.<ChemicalCardResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/qr/{qrPayload}")
    public ApiResponse<ChemicalCardResponse> getByQrPayload(
            @PathVariable String qrPayload
    ) {
        ChemicalCardResponse response = chemicalCardService.getByQrPayload(qrPayload);

        return ApiResponse.<ChemicalCardResponse>ok()
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ChemicalCardResponse> updateCard(
            @PathVariable String id,
            @Valid @RequestBody UpdateChemicalCardRequest request
    ) {
        ChemicalCardResponse response = chemicalCardService.updateCard(id, request);

        return ApiResponse.<ChemicalCardResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/active")
    public ApiResponse<ChemicalCardResponse> updateActiveStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateActiveRequest request
    ) {
        ChemicalCardResponse response =
                chemicalCardService.updateActiveStatus(id, request.getActive());

        return ApiResponse.<ChemicalCardResponse>ok()
                .data(response)
                .build();
    }
}