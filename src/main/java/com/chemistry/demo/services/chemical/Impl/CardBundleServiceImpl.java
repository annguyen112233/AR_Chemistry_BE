package com.chemistry.demo.services.chemical.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.CardBundleResponse;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.entity.CardBundle;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.mapper.CardBundleMapper;
import com.chemistry.demo.mapper.ChemicalCardMapper;
import com.chemistry.demo.repository.CardBundleRepository;
import com.chemistry.demo.services.chemical.CardBundleService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardBundleServiceImpl implements CardBundleService{
    private final CardBundleRepository cardBundleRepository;
    private final CardBundleMapper cardBundleMapper;


    @Override
    public PageResponse<CardBundleResponse> getCardBundlesShop(Pageable pageable) {
        Page<CardBundle> cardBundles =
                cardBundleRepository.findByActiveTrueAndPurchasableTrue(pageable);

        return PageResponseUtils.toPageResponse(
                cardBundles,
                cardBundleMapper::toCardBundleResponse
        );
    }

    @Override
    public PageResponse<CardBundleResponse> getChemicalCardsForStaff(Boolean purchasable, Pageable pageable) {
        Page<CardBundle> cardBundle = purchasable == null
                ? cardBundleRepository.findAll(pageable)
                : cardBundleRepository.findByPurchasable(purchasable, pageable);

        return PageResponseUtils.toPageResponse(
                cardBundle,
                cardBundleMapper::toCardBundleResponse
        );
    }
}
