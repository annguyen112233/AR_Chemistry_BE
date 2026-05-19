package com.chemistry.demo.entity;

import com.chemistry.demo.enums.FeedbackPriority;
import com.chemistry.demo.enums.FeedbackStatus;
import com.chemistry.demo.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FeedbackPriority priority = FeedbackPriority.MEDIUM;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    private String imageUrl;

    private String appVersion;

    private String deviceInfo;

    @Builder.Default
    private Boolean anonymous = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}