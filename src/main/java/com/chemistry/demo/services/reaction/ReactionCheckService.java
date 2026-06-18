package com.chemistry.demo.services.reaction;


import com.chemistry.demo.dto.request.reaction.CheckReactionRequest;
import com.chemistry.demo.dto.response.reaction.CheckReactionResponse;

public interface ReactionCheckService {
    CheckReactionResponse checkReaction(CheckReactionRequest request);
}
