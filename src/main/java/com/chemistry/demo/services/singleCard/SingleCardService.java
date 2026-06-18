package com.chemistry.demo.services.singleCard;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.singleCard.SingleCardShopResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SingleCardService {
    PageResponse<SingleCardShopResponse> getActiveSingleCards(Pageable pageable);
    PageResponse<SingleCardShopResponse> getSingleCardsForAdmin(Pageable pageable);
}
