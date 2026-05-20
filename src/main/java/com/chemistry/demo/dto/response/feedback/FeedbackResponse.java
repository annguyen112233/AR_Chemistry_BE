package com.chemistry.demo.dto.response.feedback;

import com.chemistry.demo.enums.FeedbackPriority;
import com.chemistry.demo.enums.FeedbackStatus;
import com.chemistry.demo.enums.FeedbackType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private String id;

    private String title;

    private String content;

    private FeedbackType type;

    private FeedbackStatus status;

    private FeedbackPriority priority;

    private Boolean anonymous;

    private String displayName;

    private String imageUrl;

    private String adminReply;

    private String appVersion;

    private String deviceInfo;

    private Instant createdAt;

    private Instant updatedAt;
}
