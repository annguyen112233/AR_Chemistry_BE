package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "element_details",
        indexes = {
                @Index(name = "idx_element_atomic_number", columnList = "atomicNumber"),
                @Index(name = "idx_element_symbol", columnList = "symbol")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false, unique = true)
    private ChemicalSubstance substance;

    @Column(nullable = false, unique = true)
    private Integer atomicNumber;

    @Column(nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(length = 255)
    private String periodicCategory;

    @Column(precision = 12, scale = 6)
    private BigDecimal atomicMass;

    private Integer period;

    private Integer groupNumber;
}