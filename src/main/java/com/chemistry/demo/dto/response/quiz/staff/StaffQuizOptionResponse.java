package com.chemistry.demo.dto.response.quiz.staff;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffQuizOptionResponse {

    private String id;

    private String optionKey;

    private String optionText;

    private Integer optionOrder;
}