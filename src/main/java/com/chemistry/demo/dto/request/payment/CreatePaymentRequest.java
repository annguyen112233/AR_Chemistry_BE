package com.chemistry.demo.dto.request.payment;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    String packageId;
    String proofImageUrl;
}
