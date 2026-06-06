package com.chemistry.demo.entity;

import com.chemistry.demo.enums.AccessSource;
import com.chemistry.demo.enums.AccessStatus;
import com.chemistry.demo.enums.AccessType;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessType accessType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessSource source;

    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessStatus status;

    private String referenceId;
}