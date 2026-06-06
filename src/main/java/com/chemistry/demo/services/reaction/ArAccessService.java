package com.chemistry.demo.services.reaction;

import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.entity.User;

public interface ArAccessService {
    boolean canScanAR(User user);
    ArAccessResponse getMyArAccess();
}
