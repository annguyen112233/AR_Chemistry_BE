package com.chemistry.demo.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "chemical_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemicalCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private Integer atomicNumber;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;

    private String category;

    private BigDecimal atomicMass;

    private Integer period;

    private Integer groupNumber;

    private BigDecimal price;

    private Boolean active;

    private Boolean purchasable;
}
