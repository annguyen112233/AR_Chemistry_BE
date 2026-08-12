package com.chemistry.demo.services.chemical;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.CardBundleResponse;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import org.springframework.data.domain.Pageable;

public interface CardBundleService {
    PageResponse<CardBundleResponse> getCardBundlesShop(Pageable pageable);

    PageResponse<CardBundleResponse> getChemicalCardsForStaff(Boolean purchasable, Pageable pageable);

    void deleteCardBundle(String id);
}
