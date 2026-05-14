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
public class FeedbackListResponse {

    private String id;

    private String title;

    private FeedbackType type;

    private FeedbackStatus status;

    private FeedbackPriority priority;

    private String displayName;

    private Boolean anonymous;

    private Instant createdAt;
}