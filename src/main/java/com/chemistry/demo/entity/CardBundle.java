package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "card_bundles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    private BigDecimal originalPrice;

    @Column(nullable = false)
    private BigDecimal discountedPrice;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean purchasable = true;

    @ManyToMany
    @JoinTable(
            name = "card_bundle_items",
            joinColumns = @JoinColumn(name = "bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "chemical_card_id")
    )
    @Builder.Default
    private Set<ChemicalCard> cards = new HashSet<>();
}
