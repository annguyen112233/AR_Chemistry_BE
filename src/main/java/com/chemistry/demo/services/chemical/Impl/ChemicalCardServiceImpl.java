package com.chemistry.demo.services.chemical.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.mapper.ChemicalCardMapper;
import com.chemistry.demo.repository.ChemicalCardRepository;
import com.chemistry.demo.services.chemical.ChemicalCardService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemicalCardServiceImpl implements ChemicalCardService {
    private final ChemicalCardRepository chemicalCardRepository;
    private final ChemicalCardMapper chemicalCardMapper;

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public PageResponse<ChemicalCardResponse> getChemicalCardsShop(Pageable pageable) {

        Page<ChemicalCard> chemicalCards =
                chemicalCardRepository.findByActiveTrueAndPurchasableTrue(pageable);

        return PageResponseUtils.toPageResponse(
                chemicalCards,
                chemicalCardMapper::toChemicalCardResponse
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public PageResponse<ChemicalCardResponse> getChemicalCardsForStaff(
            Boolean purchasable,
            Pageable pageable
    ) {

        Page<ChemicalCard> chemicalCards = purchasable == null
                ? chemicalCardRepository.findAll(pageable)
                : chemicalCardRepository.findByPurchasable(purchasable, pageable);

        return PageResponseUtils.toPageResponse(
                chemicalCards,
                chemicalCardMapper::toChemicalCardResponse
        );
    }
}
