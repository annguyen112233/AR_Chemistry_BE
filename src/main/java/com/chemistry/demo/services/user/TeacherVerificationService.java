package com.chemistry.demo.services.user;

import com.chemistry.demo.dto.request.role.RejectTeacherRequest;

public interface TeacherVerificationService {
    String approveTeacher(String cognitoSub);

    String rejectTeacher(String cognitoSub, RejectTeacherRequest reason);
}
