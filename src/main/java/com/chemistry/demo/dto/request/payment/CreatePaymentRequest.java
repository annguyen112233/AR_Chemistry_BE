package com.chemistry.demo.dto.request.payment;

import com.chemistry.demo.enums.PaymentItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    @NotBlank(message = "Item id is required")
    private String itemId;

    @NotNull(message = "Item type is required")
    private PaymentItemType itemType;

    @NotBlank(message = "Proof image url is required")
    String proofImageUrl;
}
