package com.chemistry.demo.controller.singleCard;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.singleCard.SingleCardShopResponse;
import com.chemistry.demo.services.singleCard.SingleCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/single-cards")
@RequiredArgsConstructor
public class SingleCardController {

    private final SingleCardService singleCardService;

    /**
     * User/shop lấy danh sách card đang bán.
     */
    @GetMapping
    public ApiResponse<PageResponse<SingleCardShopResponse>> getActiveSingleCards(
            @PageableDefault(
                    size = 20,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<SingleCardShopResponse>>ok()
                .data(singleCardService.getActiveSingleCards(pageable))
                .build();
    }

    /**
     * Admin xem toàn bộ card, bao gồm inactive.
     */
    @GetMapping("/admin")
    public ApiResponse<PageResponse<SingleCardShopResponse>> getSingleCardsForAdmin(
            @PageableDefault(
                    size = 30,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<SingleCardShopResponse>>ok()
                .data(singleCardService.getSingleCardsForAdmin(pageable))
                .build();
    }
}