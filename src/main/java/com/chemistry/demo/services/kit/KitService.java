package com.chemistry.demo.services.kit;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.kit.AddKitItemRequest;
import com.chemistry.demo.dto.request.kit.CreateFullKitRequest;
import com.chemistry.demo.dto.request.kit.CreateKitRequest;
import com.chemistry.demo.dto.request.kit.UpdateKitRequest;
import com.chemistry.demo.dto.response.kit.KitResponse;
import org.springframework.data.domain.Pageable;

public interface KitService {
    KitResponse createKit(CreateKitRequest request);
    KitResponse createFullKitFromIncludedSubstances(CreateFullKitRequest request);
    PageResponse<KitResponse> getKits(Boolean active, Pageable pageable);
    KitResponse getKitById(String id);
    KitResponse getKitByCode(String code);
    KitResponse updateKit(String id, UpdateKitRequest request);
    KitResponse addItemToKit(String kitId, AddKitItemRequest request);
    KitResponse removeItemFromKit(String kitId, String substanceId);
    KitResponse updateActiveStatus(String id, Boolean active);
    KitResponse restoreItemToKit(String kitId, String substanceId);
}
