package com.chemistry.demo.dto.response.packages;

import com.chemistry.demo.enums.PackageType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {


    private String id;

    private PackageType packageType;

    private String name;

    private BigDecimal price;

    private Integer durationDays;
}
