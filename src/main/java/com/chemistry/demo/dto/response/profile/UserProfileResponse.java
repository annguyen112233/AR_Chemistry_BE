package com.chemistry.demo.dto.response.profile;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;

}
