package com.chemistry.demo.dto.request.role;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectTeacherRequest {
    private String rejectionReason;
}
