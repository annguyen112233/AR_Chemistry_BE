package com.chemistry.demo.controller.singleCardPurchase;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.payment.BuySingleCardRequest;
import com.chemistry.demo.dto.response.singleCardPurchase.MySingleCardPurchaseResponse;
import com.chemistry.demo.dto.response.singleCardPurchase.SingleCardPurchaseResponse;
import com.chemistry.demo.services.singleCardPurchase.SingleCardPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/single-card-purchases")
@RequiredArgsConstructor
public class SingleCardPurchaseController {
    private final SingleCardPurchaseService singleCardPurchaseService;

    @PostMapping("/buy")
    public ApiResponse<SingleCardPurchaseResponse> fakeBuySingleCard(
            @Valid @RequestBody BuySingleCardRequest request
    ) {

        SingleCardPurchaseResponse response = singleCardPurchaseService.buySingleCardWithKnowledgePoint(request.getSingleCardId());

        return ApiResponse.<SingleCardPurchaseResponse>ok()
                .data(response)
                .build();

    }

    @GetMapping("/my")
    public ApiResponse<PageResponse<MySingleCardPurchaseResponse>> getMySingleCardPurchases(
            @PageableDefault(
                    size = 20,
                    sort = "purchasedAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<MySingleCardPurchaseResponse> response =
                singleCardPurchaseService.getMySingleCardPurchases(pageable);

        return ApiResponse.<PageResponse<MySingleCardPurchaseResponse>>ok()
                .data(response)
                .build();
    }
}
