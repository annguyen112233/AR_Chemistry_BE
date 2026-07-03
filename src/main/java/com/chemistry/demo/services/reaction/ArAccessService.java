package com.chemistry.demo.services.reaction;

import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.dto.response.reaction.PackageOwnershipResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.User;

import java.util.List;

public interface ArAccessService {
    ArAccessResponse getMyArAccess();
    boolean canScanSubstances(User user, List<ChemicalSubstance> substances);
    PackageOwnershipResponse getMyAr30DaysOwnership();

}
