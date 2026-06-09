package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "compound_details",
        indexes = {
                @Index(name = "idx_compound_substance", columnList = "substance_id"),
                @Index(name = "idx_compound_iupac_name", columnList = "iupacName"),
                @Index(name = "idx_compound_cas_number", columnList = "casNumber")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompoundDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false, unique = true)
    private ChemicalSubstance substance;

    /**
     * Tên IUPAC nếu có.
     * Ví dụ: sodium hydroxide, hydrogen chloride...
     */
    @Column(length = 255)
    private String iupacName;

    /**
     * Mã CAS nếu sau này bạn cần.
     * Ví dụ HCl: 7647-01-0
     */
    @Column(length = 100)
    private String casNumber;

    /**
     * Dạng phân loại chi tiết hơn.
     * Ví dụ:
     * STRONG_ACID, STRONG_BASE, SALT, OXIDE, GAS...
     */
    @Column(length = 100)
    private String compoundClass;

    /**
     * Công dụng trong bộ kit.
     * Ví dụ: dùng để phản ứng với kim loại tạo khí H2.
     */
    @Column(length = 1000)
    private String usageNote;

    /**
     * Có phải sản phẩm phản ứng sinh ra trong AR không.
     * Ví dụ:
     * ZnCl2, H2 thường là sản phẩm reaction.
     */
    @Column(nullable = false)
    private Boolean reactionProductOnly;

    /**
     * Có phải chất vật lý thật trong hộp không.
     * Thường giống includedInFullKit, nhưng để detail cũng được nếu bạn muốn xem nhanh.
     */
    @Column(nullable = false)
    private Boolean physicalInKit;
}