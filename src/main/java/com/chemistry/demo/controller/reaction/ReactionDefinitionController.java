package com.chemistry.demo.controller.reaction;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.reaction.ReactionDefinitionResponse;
import com.chemistry.demo.dto.response.reaction.ReactionSummaryResponse;
import com.chemistry.demo.services.reaction.ReactionDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reactions/definitions")
@RequiredArgsConstructor
public class ReactionDefinitionController {
    private final ReactionDefinitionService reactionDefinitionService;

    @GetMapping("/summary")
    public ApiResponse<ReactionSummaryResponse> getReactionSummary() {
        ReactionSummaryResponse response =
                reactionDefinitionService.getReactionSummary();

        return ApiResponse.<ReactionSummaryResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ReactionDefinitionResponse>> getActiveReactions(
            @PageableDefault(
                    size = 30,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<ReactionDefinitionResponse> response =
                reactionDefinitionService.getActiveReactions(pageable);

        return ApiResponse.<PageResponse<ReactionDefinitionResponse>>ok()
                .data(response)
                .build();
    }
}
