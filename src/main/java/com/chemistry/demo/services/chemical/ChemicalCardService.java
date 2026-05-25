package com.chemistry.demo.services.chemical;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import org.springframework.data.domain.Pageable;

public interface ChemicalCardService {
    PageResponse<ChemicalCardResponse> getChemicalCardsShop(Pageable pageable);

    PageResponse<ChemicalCardResponse> getChemicalCardsForStaff(Boolean purchasable, Pageable pageable);
}
