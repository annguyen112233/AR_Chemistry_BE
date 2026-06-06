package com.chemistry.demo.dto.request.reaction;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckReactionRequest {

    @NotEmpty
    private List<String> qrPayloads;
}
