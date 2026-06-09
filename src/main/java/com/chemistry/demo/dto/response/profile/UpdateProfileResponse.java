package com.chemistry.demo.dto.response.profile;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileResponse {
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;
}
