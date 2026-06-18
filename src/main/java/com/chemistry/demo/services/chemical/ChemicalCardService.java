package com.chemistry.demo.services.chemical;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.chemical.CreateChemicalCardRequest;
import com.chemistry.demo.dto.request.chemical.UpdateChemicalCardRequest;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import org.springframework.data.domain.Pageable;

public interface ChemicalCardService {
    ChemicalCardResponse createCard(CreateChemicalCardRequest request);

    PageResponse<ChemicalCardResponse> getCards(
            Boolean active,
            String substanceId,
            Pageable pageable
    );

    ChemicalCardResponse getById(String id);
    ChemicalCardResponse getByCardCode(String cardCode);
    ChemicalCardResponse getByQrPayload(String qrPayload);
    ChemicalCardResponse updateCard(String id, UpdateChemicalCardRequest request);
    ChemicalCardResponse updateActiveStatus(String id, Boolean active);

}
