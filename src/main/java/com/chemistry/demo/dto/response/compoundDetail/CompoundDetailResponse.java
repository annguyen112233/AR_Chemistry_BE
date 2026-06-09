package com.chemistry.demo.dto.response.compoundDetail;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompoundDetailResponse {

    private String iupacName;

    private String casNumber;

    private String compoundClass;

    private String usageNote;

    private Boolean reactionProductOnly;

    private Boolean physicalInKit;
}
