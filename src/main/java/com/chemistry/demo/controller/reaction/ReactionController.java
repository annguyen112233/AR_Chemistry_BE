package com.chemistry.demo.controller.reaction;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.reaction.CreateReactionRequest;
import com.chemistry.demo.dto.request.reaction.UpdateReactionRequest;
import com.chemistry.demo.dto.request.subtance.UpdateActiveRequest;
import com.chemistry.demo.dto.response.reaction.ReactionResponse;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionType;
import com.chemistry.demo.services.reaction.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping
    public ApiResponse<ReactionResponse> createReaction(
            @Valid @RequestBody CreateReactionRequest request
    ) {
        ReactionResponse response = reactionService.createReaction(request);

        return ApiResponse.<ReactionResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ReactionResponse>> getReactions(
            @RequestParam(required = false)
            Boolean active,

            @RequestParam(required = false)
            ReactionType reactionType,

            @RequestParam(required = false)
            ArSceneKey arSceneKey,

            @RequestParam(required = false)
            ReactionCategory reactionCategory,

            @RequestParam(required = false)
            Integer grade,

            @PageableDefault(
                    size = 20,
                    sort = "code",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        PageResponse<ReactionResponse> response =
                reactionService.getReactions(
                        active,
                        reactionType,
                        arSceneKey,
                        reactionCategory,
                        grade,
                        pageable
                );

        return ApiResponse
                .<PageResponse<ReactionResponse>>ok()
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ReactionResponse> getReactionById(
            @PathVariable String id
    ) {
        ReactionResponse response = reactionService.getReactionById(id);

        return ApiResponse.<ReactionResponse>ok()
                .data(response)
                .build();
    }

    @GetMapping("/code/{code}")
    public ApiResponse<ReactionResponse> getReactionByCode(
            @PathVariable String code
    ) {
        ReactionResponse response = reactionService.getReactionByCode(code);

        return ApiResponse.<ReactionResponse>ok()
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ReactionResponse> updateReaction(
            @PathVariable String id,
            @Valid @RequestBody UpdateReactionRequest request
    ) {
        ReactionResponse response = reactionService.updateReaction(id, request);

        return ApiResponse.<ReactionResponse>ok()
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/active")
    public ApiResponse<ReactionResponse> updateActiveStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateActiveRequest request
    ) {
        ReactionResponse response = reactionService.updateActiveStatus(
                id,
                request.getActive()
        );

        return ApiResponse.<ReactionResponse>ok()
                .data(response)
                .build();
    }
}
