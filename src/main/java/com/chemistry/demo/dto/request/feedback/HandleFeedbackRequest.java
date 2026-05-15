package com.chemistry.demo.dto.request.feedback;

import com.chemistry.demo.enums.FeedbackPriority;
import com.chemistry.demo.enums.FeedbackStatus;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HandleFeedbackRequest {
    private FeedbackStatus status;

    private FeedbackPriority priority;

    private String adminReply;
}
