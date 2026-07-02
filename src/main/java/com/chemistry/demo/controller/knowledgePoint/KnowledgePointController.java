package com.chemistry.demo.controller.knowledgePoint;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.response.knowledgePoint.KnowledgePointWalletResponse;
import com.chemistry.demo.services.knowledgePoint.KnowledgePointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/knowledge-points")
@RequiredArgsConstructor
public class KnowledgePointController {

    private final KnowledgePointService knowledgePointService;

    @GetMapping("/me")
    public ApiResponse<KnowledgePointWalletResponse> getMyWallet() {
        return ApiResponse.<KnowledgePointWalletResponse>ok()
                .data(knowledgePointService.getMyWallet())
                .message("Get Knowledge Point wallet successfully")
                .build();
    }
}