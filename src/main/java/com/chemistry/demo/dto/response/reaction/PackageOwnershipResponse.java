package com.chemistry.demo.dto.response.reaction;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageOwnershipResponse {
    private boolean owned;
    private String accessType;
    private String message;
}