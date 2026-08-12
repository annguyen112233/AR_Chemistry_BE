package com.chemistry.demo.services.singleCard.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.singleCard.SingleCardShopResponse;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.SingleCard;
import com.chemistry.demo.mapper.SingleCardMapper;
import com.chemistry.demo.repository.ChemicalCardRepository;
import com.chemistry.demo.repository.SingleCardRepository;
import com.chemistry.demo.services.aws.S3Service;
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
    private final ChemicalCardRepository chemicalCardRepository;
    private final S3Service s3Service;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SingleCardShopResponse> getActiveSingleCards(Pageable pageable) {
        Page<SingleCard> singleCards =
                singleCardRepository.findActiveSingleCardsWithActiveChemicalCard(pageable);

        return PageResponseUtils.toPageResponse(singleCards, singleCard -> {
            SingleCardShopResponse response = SingleCardMapper.toShopResponse(singleCard);

            ChemicalSubstance substance = singleCard.getSubstance();

            if (substance == null) {
                return response;
            }

            ChemicalCard card = chemicalCardRepository
                    .findFirstBySubstance_IdAndActiveTrue(substance.getId())
                    .orElse(null);


            response.setFrontImageUrl(
                    card == null || card.getFrontImageKey() == null
                            ? null
                            : s3Service.generatePresignedGetUrl(card.getFrontImageKey())
            );

            response.setBackImageUrl(
                    card == null || card.getBackImageKey() == null
                            ? null
                            : s3Service.generatePresignedGetUrl(card.getBackImageKey())
            );

            return response;
        });
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
