package com.chemistry.demo.dto.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body cho API chấm câu trả lời AI: 1 = 👍, -1 = 👎, 0 = bỏ chấm. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateMessageRequest {

    @NotNull
    @Min(-1)
    @Max(1)
    private Integer rating;
}
