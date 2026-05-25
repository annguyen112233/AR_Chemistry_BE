package com.chemistry.demo.controller.chemical;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.services.chemical.ChemicalCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/chemical-cards")
@RequiredArgsConstructor
public class ChemicalCardController {
    private final ChemicalCardService chemicalCardService;


    @GetMapping
    public ApiResponse<PageResponse<ChemicalCardResponse>> getAllChemicalCards(
            @PageableDefault(
                    size = 10,
                    sort = "atomicNumber",
                    direction = Sort.Direction.DESC

            ) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<ChemicalCardResponse>>ok()
                .data(chemicalCardService.getChemicalCardsShop(pageable))
                .build();
    }
}
