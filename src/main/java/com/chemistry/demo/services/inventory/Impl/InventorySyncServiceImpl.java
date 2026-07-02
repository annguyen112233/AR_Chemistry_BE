package com.chemistry.demo.services.inventory.Impl;

import com.chemistry.demo.dto.response.inventory.SyncInventoryResponse;
import com.chemistry.demo.dto.response.inventory.SyncInventoryUserResult;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.ActivationCodeStatus;
import com.chemistry.demo.enums.InventorySource;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.KitErrorCode;
import com.chemistry.demo.repository.KitActivationCodeRepository;
import com.chemistry.demo.repository.KitItemRepository;
import com.chemistry.demo.repository.KitRepository;
import com.chemistry.demo.repository.UserInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InventorySyncServiceImpl implements com.chemistry.demo.services.inventory.InventorySyncService {
    private final KitRepository kitRepository;
    private final KitItemRepository kitItemRepository;
    private final KitActivationCodeRepository kitActivationCodeRepository;
    private final UserInventoryRepository userInventoryRepository;
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SyncInventoryResponse syncKitInventory(String kitId) {
        Kit kit = kitRepository.findById(kitId)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, kitId));

        if (!Boolean.TRUE.equals(kit.getActive())) {
            throw new RuntimeException("Kit is inactive: " + kit.getCode());
        }

        List<KitItem> activeKitItems = kitItemRepository.findByKitIdAndActiveTrue(kitId);

        if (activeKitItems.isEmpty()) {
            throw new RuntimeException("Kit has no active items: " + kit.getCode());
        }

        List<KitActivationCode> usedCodes =
                kitActivationCodeRepository.findByKitIdAndStatus(
                        kitId,
                        ActivationCodeStatus.USED
                );

        Map<String, User> activatedUsers = new LinkedHashMap<>();

        for (KitActivationCode code : usedCodes) {
            User user = code.getUsedByUser();

            if (user != null && user.getCognitoSub() != null) {
                activatedUsers.put(user.getCognitoSub(), user);
            }
        }

        Instant now = Instant.now();

        int grantedInventoryCount = 0;
        List<SyncInventoryUserResult> userResults = new ArrayList<>();

        for (User user : activatedUsers.values()) {
            List<String> grantedFormulas = new ArrayList<>();

            for (KitItem kitItem : activeKitItems) {
                ChemicalSubstance substance = kitItem.getSubstance();

                if (substance == null || !Boolean.TRUE.equals(substance.getActive())) {
                    continue;
                }

                Optional<UserInventory> optionalInventory =
                        userInventoryRepository.findByUserAndSubstance(user, substance);

                if (optionalInventory.isEmpty()) {
                    UserInventory inventory = UserInventory.builder()
                            .user(user)
                            .substance(substance)
                            .source(InventorySource.KIT_SYNC)
                            .sourceRef(kit.getCode())
                            .quantity(
                                    kitItem.getQuantity() != null
                                            ? kitItem.getQuantity()
                                            : 1
                            )
                            .active(true)
                            .acquiredAt(now)
                            .expiresAt(null)
                            .build();

                    userInventoryRepository.save(inventory);

                    grantedInventoryCount++;
                    grantedFormulas.add(substance.getFormula());
                } else {
                    UserInventory inventory = optionalInventory.get();

                    if (!Boolean.TRUE.equals(inventory.getActive())) {
                        inventory.setActive(true);
                        inventory.setSource(InventorySource.KIT_SYNC);
                        inventory.setSourceRef(kit.getCode());

                        if (inventory.getQuantity() == null || inventory.getQuantity() <= 0) {
                            inventory.setQuantity(
                                    kitItem.getQuantity() != null
                                            ? kitItem.getQuantity()
                                            : 1
                            );
                        }

                        if (inventory.getAcquiredAt() == null) {
                            inventory.setAcquiredAt(now);
                        }

                        userInventoryRepository.save(inventory);

                        grantedInventoryCount++;
                        grantedFormulas.add(substance.getFormula());
                    }
                }
            }

            if (!grantedFormulas.isEmpty()) {
                userResults.add(
                        SyncInventoryUserResult.builder()
                                .userId(user.getCognitoSub())
                                .grantedFormulas(grantedFormulas)
                                .build()
                );
            }
        }

        return SyncInventoryResponse.builder()
                .success(true)
                .message("Đồng bộ inventory cho kit thành công")
                .kitId(kit.getId())
                .kitCode(kit.getCode())
                .kitName(kit.getName())
                .activatedUserCount(activatedUsers.size())
                .kitItemCount(activeKitItems.size())
                .grantedInventoryCount(grantedInventoryCount)
                .userResults(userResults)
                .build();
    }
}
