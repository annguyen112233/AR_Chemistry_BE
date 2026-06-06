package com.chemistry.demo.services.library;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.library.LibraryCardResponse;
import com.chemistry.demo.dto.response.library.LibrarySummaryResponse;
import org.springframework.data.domain.Pageable;

public interface LibraryService {
    PageResponse<LibraryCardResponse> getLibraryCards(Pageable pageable);
    LibrarySummaryResponse getLibrarySummary();
}
