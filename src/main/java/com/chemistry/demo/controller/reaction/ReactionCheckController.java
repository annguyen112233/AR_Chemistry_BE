package com.chemistry.demo.controller.reaction;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.reaction.CheckReactionRequest;
import com.chemistry.demo.dto.response.reaction.CheckReactionResponse;
import com.chemistry.demo.services.reaction.ReactionCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionCheckController {

    private final ReactionCheckService reactionCheckService;

    @PostMapping("/check")
    public ApiResponse<CheckReactionResponse> checkReaction(
            @Valid @RequestBody CheckReactionRequest request
    ) {
        CheckReactionResponse response = reactionCheckService.checkReaction(request);

        return ApiResponse.<CheckReactionResponse>ok()
                .data(response)
                .build();
    }
}
