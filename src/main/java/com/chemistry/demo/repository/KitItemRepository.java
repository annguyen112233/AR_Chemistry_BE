package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Kit;
import com.chemistry.demo.entity.KitItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KitItemRepository extends JpaRepository<KitItem, String> {

    List<KitItem> findByKitId(String kitId);

    List<KitItem> findByKitIdAndActiveTrue(String kitId);

    boolean existsByKitIdAndSubstanceId(String kitId, String substanceId);

    Optional<KitItem> findByKitIdAndSubstanceId(
            String kitId,
            String substanceId
    );

    List<KitItem> findByKit(Kit kit);
}
