package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.CardBundleResponse;
import com.chemistry.demo.services.chemical.CardBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/card-bundles")
@RequiredArgsConstructor
public class StaffCardBundleController {
    private final CardBundleService cardBundleService;

    @GetMapping
    public ApiResponse<PageResponse<CardBundleResponse>> getChemicalCardsForStaff(
            @RequestParam(required = false) Boolean purchasable,
            @PageableDefault(
                    size = 10
            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<CardBundleResponse>>ok()
                .data(cardBundleService.getChemicalCardsForStaff(purchasable, pageable))
                .build();
    }

}
