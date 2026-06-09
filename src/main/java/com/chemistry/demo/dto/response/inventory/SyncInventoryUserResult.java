package com.chemistry.demo.dto.response.inventory;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncInventoryUserResult {

    private String userId;

    private List<String> grantedFormulas;
}
