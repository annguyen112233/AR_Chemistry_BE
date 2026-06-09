package com.chemistry.demo.entity;

import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import com.chemistry.demo.enums.SubstanceState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "chemical_substances",
        indexes = {
                @Index(name = "idx_substance_formula", columnList = "formula"),
                @Index(name = "idx_substance_active", columnList = "active"),
                @Index(name = "idx_substance_group", columnList = "chemicalGroup")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemicalSubstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String formula;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String vietnameseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChemicalSubstanceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChemicalGroup chemicalGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SubstanceState state;

    @Column(precision = 12, scale = 6)
    private BigDecimal molarMass;

    @Column(nullable = false)
    private Boolean active;

    /**
     * Chất này có nằm trong hộp Full Kit vật lý không.
     * Ví dụ:
     * Zn, HCl -> true
     * ZnCl2, H2 sản phẩm phản ứng -> có thể false
     */
    @Column(nullable = false)
    private Boolean includedInFullKit;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String safetyNote;
}
