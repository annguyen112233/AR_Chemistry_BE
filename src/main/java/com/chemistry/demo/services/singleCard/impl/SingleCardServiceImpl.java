package com.chemistry.demo.services.singleCard.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.singleCard.SingleCardShopResponse;
import com.chemistry.demo.entity.SingleCard;
import com.chemistry.demo.mapper.SingleCardMapper;
import com.chemistry.demo.repository.SingleCardRepository;
import com.chemistry.demo.services.singleCard.SingleCardService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SingleCardServiceImpl implements SingleCardService {
    private final SingleCardRepository singleCardRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SingleCardShopResponse> getActiveSingleCards(Pageable pageable) {
        Page<SingleCard> singleCards = singleCardRepository.findByActiveTrue(pageable);

        return PageResponseUtils.toPageResponse(
                singleCards,
                SingleCardMapper::toShopResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<SingleCardShopResponse> getSingleCardsForAdmin(Pageable pageable) {
        Page<SingleCard> singleCards = singleCardRepository.findAll(pageable);

        return PageResponseUtils.toPageResponse(
                singleCards,
                SingleCardMapper::toShopResponse
        );
    }
}
