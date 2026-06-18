package com.chemistry.demo.entity;

import com.chemistry.demo.enums.ActivationCodeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "kit_activation_codes",
        indexes = {
                @Index(name = "idx_activation_code", columnList = "code"),
                @Index(name = "idx_activation_status", columnList = "status"),
                @Index(name = "idx_activation_kit", columnList = "kit_id"),
                @Index(name = "idx_activation_used_by", columnList = "used_by_user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitActivationCode extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_id", nullable = false)
    private Kit kit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivationCodeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_user_id")
    private User usedByUser;

    private Instant usedAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private Boolean active;

    @Column(length = 1000)
    private String note;
}
