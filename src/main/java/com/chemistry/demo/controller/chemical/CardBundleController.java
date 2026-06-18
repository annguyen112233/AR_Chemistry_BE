package com.chemistry.demo.controller.chemical;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.CardBundleResponse;
import com.chemistry.demo.services.chemical.CardBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/card-bundles")
@RequiredArgsConstructor
public class CardBundleController {
    private final CardBundleService cardBundleService;

    @GetMapping
    public ApiResponse<PageResponse<CardBundleResponse>> getCardBundles(
            @PageableDefault(
                    size = 10
            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<CardBundleResponse>>ok()
                .data(cardBundleService.getCardBundlesShop(pageable))
                .build();
    }
}
