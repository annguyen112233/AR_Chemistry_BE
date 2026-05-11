package com.chemistry.demo.services.user;

import com.chemistry.demo.dto.request.RejectTeacherRequest;

public interface TeacherVerificationService {
    String approveTeacher(String cognitoSub);

    String rejectTeacher(String cognitoSub, RejectTeacherRequest reason);
}
