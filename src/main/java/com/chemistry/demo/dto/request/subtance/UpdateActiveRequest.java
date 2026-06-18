package com.chemistry.demo.dto.request.subtance;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateActiveRequest {

    @NotNull
    private Boolean active;
}
