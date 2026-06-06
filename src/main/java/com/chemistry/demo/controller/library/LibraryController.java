package com.chemistry.demo.controller.library;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.library.LibraryCardResponse;
import com.chemistry.demo.dto.response.library.LibrarySummaryResponse;
import com.chemistry.demo.services.library.LibraryService;
import com.chemistry.demo.services.subtance.SubstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping("/cards")
    public ApiResponse<PageResponse<LibraryCardResponse>> getLibraryCards(
            @PageableDefault(
                    size = 30,
                    sort = "formula",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<LibraryCardResponse> response =
                libraryService.getLibraryCards(pageable);

        return ApiResponse.<PageResponse<LibraryCardResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping("/summary")
    public ApiResponse<LibrarySummaryResponse> getLibrarySummary() {
        LibrarySummaryResponse response = libraryService.getLibrarySummary();

        return ApiResponse.<LibrarySummaryResponse>ok()
                .data(response)
                .build();
    }
}