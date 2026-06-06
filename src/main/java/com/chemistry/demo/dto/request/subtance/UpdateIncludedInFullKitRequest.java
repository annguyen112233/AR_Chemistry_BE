package com.chemistry.demo.dto.request.subtance;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateIncludedInFullKitRequest {

    @NotNull
    private Boolean includedInFullKit;
}
