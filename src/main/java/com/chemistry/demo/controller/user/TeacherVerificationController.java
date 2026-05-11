package com.chemistry.demo.controller.user;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.RejectTeacherRequest;
import com.chemistry.demo.services.user.TeacherVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/teacher-verifications")
@RequiredArgsConstructor
public class TeacherVerificationController {
    private final TeacherVerificationService verificationService;

    @PutMapping("/{cognitoSub}/approve")
    public ApiResponse<String> approve(@PathVariable String cognitoSub) {
        return ApiResponse.<String>ok()
                .data(verificationService.approveTeacher(cognitoSub))
                .build();
    }

    @PutMapping("/{cognitoSub}/reject")
    public ApiResponse<String> reject(
            @PathVariable String cognitoSub,
            @RequestBody RejectTeacherRequest reason) {
        return ApiResponse.<String>ok()
                .data(verificationService.rejectTeacher(cognitoSub, reason))
                .build();
    }
}
