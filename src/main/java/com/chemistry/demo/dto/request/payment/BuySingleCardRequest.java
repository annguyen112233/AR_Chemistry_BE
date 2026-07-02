package com.chemistry.demo.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuySingleCardRequest {

    @NotBlank
    private String singleCardId;
}