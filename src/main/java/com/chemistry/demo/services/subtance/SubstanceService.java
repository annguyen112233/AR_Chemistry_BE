package com.chemistry.demo.services.subtance;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.subtance.CreateSubstanceRequest;
import com.chemistry.demo.dto.request.subtance.UpdateSubstanceRequest;
import com.chemistry.demo.dto.response.library.LibraryCardResponse;
import com.chemistry.demo.dto.response.library.LibrarySummaryResponse;
import com.chemistry.demo.dto.response.substance.SubstanceResponse;
import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import org.springframework.data.domain.Pageable;

public interface SubstanceService {
    SubstanceResponse createSubstance(CreateSubstanceRequest request);
    PageResponse<SubstanceResponse> getSubstances(
            Boolean active,
            ChemicalGroup chemicalGroup,
            ChemicalSubstanceType type,
            Boolean includedInFullKit,
            Pageable pageable
    );

    SubstanceResponse getSubstanceById(String id);
    SubstanceResponse getSubstanceByFormula(String formula);

    SubstanceResponse updateSubstance(String id, UpdateSubstanceRequest request);
    SubstanceResponse updateActiveStatus(String id, Boolean active);
    SubstanceResponse updateIncludedInFullKit(String id, Boolean includedInFullKit);


}
