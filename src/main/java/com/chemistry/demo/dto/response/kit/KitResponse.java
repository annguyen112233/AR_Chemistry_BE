package com.chemistry.demo.dto.response.kit;

import com.chemistry.demo.dto.response.substance.SubstanceResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitResponse {
    private String id;

    private String code;

    private String name;

    private String description;

    private BigDecimal price;

    private Boolean active;

    private List<SubstanceResponse> items;
}
