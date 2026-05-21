package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PackageType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Packages {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    private String name;

    private BigDecimal price;

    private Integer durationDays;
}
