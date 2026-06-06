package com.chemistry.demo.dto.response.library;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibrarySummaryResponse {

    private long unlockedCards;

    private long totalCards;

    private double libraryProgress;
}