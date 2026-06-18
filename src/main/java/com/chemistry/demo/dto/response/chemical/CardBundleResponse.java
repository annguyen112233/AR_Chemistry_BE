package com.chemistry.demo.dto.response.chemical;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBundleResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private Boolean active;
    private Boolean purchasable;
    private List<ChemicalCardResponse> cards;
}
