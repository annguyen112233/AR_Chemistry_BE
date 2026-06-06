package com.chemistry.demo.repository;

import com.chemistry.demo.entity.KitActivationCode;
import com.chemistry.demo.enums.ActivationCodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface KitActivationCodeRepository extends JpaRepository<KitActivationCode, String> {

    Optional<KitActivationCode> findByCode(String code);

    boolean existsByCode(String code);

    Page<KitActivationCode> findByKitId(String kitId, Pageable pageable);

    Page<KitActivationCode> findByStatus(
            ActivationCodeStatus status,
            Pageable pageable
    );

    Page<KitActivationCode> findByKitIdAndStatus(
            String kitId,
            ActivationCodeStatus status,
            Pageable pageable
    );

    Page<KitActivationCode> findByUsedByUser_CognitoSub(String usedByUserCognitoSub, Pageable pageable);

    List<KitActivationCode> findByKitIdAndStatus(
            String kitId,
            ActivationCodeStatus status
    );
}
