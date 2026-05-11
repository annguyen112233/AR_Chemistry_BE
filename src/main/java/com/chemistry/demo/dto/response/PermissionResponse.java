package com.chemistry.demo.dto.response;

import com.chemistry.demo.enums.PermissionName;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse implements java.io.Serializable{
    private PermissionName name;
}
