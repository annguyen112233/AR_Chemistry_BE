package com.chemistry.demo.dto.response.inventory;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncInventoryResponse {

    private Boolean success;

    private String message;

    private String kitId;

    private String kitCode;

    private String kitName;

    private Integer activatedUserCount;

    private Integer kitItemCount;

    private Integer grantedInventoryCount;

    private List<SyncInventoryUserResult> userResults;
}
