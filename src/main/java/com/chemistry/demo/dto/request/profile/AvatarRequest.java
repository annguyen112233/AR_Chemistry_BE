package com.chemistry.demo.dto.request.profile;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvatarRequest {
    private String avatarUrl;
}
