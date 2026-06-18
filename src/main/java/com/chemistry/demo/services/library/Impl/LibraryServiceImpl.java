package com.chemistry.demo.services.library.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.library.LibraryCardResponse;
import com.chemistry.demo.dto.response.library.LibrarySummaryResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.mapper.LibraryMapper;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.UserInventoryRepository;
import com.chemistry.demo.services.library.LibraryService;
import com.chemistry.demo.utils.PageResponseUtils;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LibraryCardResponse> getLibraryCards(Pageable pageable) {
        Page<ChemicalSubstance> page =
                chemicalSubstanceRepository.findByActiveTrueAndIncludedInFullKitTrue(pageable);

        return PageResponseUtils.toPageResponse(
                page,
                LibraryMapper::toLibraryCardResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LibrarySummaryResponse getLibrarySummary() {
        User user = securityUtils.getCurrentUserCognitoSub();

        long totalCards =
                chemicalSubstanceRepository.countByActiveTrueAndIncludedInFullKitTrue();

        long unlockedCards =
                userInventoryRepository.
                        countDistinctByUserAndActiveTrueAndSubstance_ActiveTrueAndSubstance_IncludedInFullKitTrue(user);

        double progress = totalCards == 0
                ? 0.0
                : (double) unlockedCards / totalCards;

        return LibrarySummaryResponse.builder()
                .unlockedCards(unlockedCards)
                .totalCards(totalCards)
                .libraryProgress(progress)
                .build();
    }
}
