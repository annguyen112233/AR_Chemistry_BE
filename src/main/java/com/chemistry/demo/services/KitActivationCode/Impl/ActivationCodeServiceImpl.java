package com.chemistry.demo.services.KitActivationCode.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.KitActivationCode.GenerateActivationCodesRequest;
import com.chemistry.demo.dto.request.KitActivationCode.UpdateActivationCodeStatusRequest;
import com.chemistry.demo.dto.response.KitActivationCode.ActivationCodeResponse;
import com.chemistry.demo.entity.Kit;
import com.chemistry.demo.entity.KitActivationCode;
import com.chemistry.demo.enums.ActivationCodeStatus;
import com.chemistry.demo.exception.ActivationCodeErrorCode;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.KitErrorCode;
import com.chemistry.demo.mapper.ActivationCodeMapper;
import com.chemistry.demo.repository.KitActivationCodeRepository;
import com.chemistry.demo.repository.KitRepository;
import com.chemistry.demo.services.KitActivationCode.ActivationCodeService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivationCodeServiceImpl implements ActivationCodeService {
    private static final String CODE_PREFIX = "KIT-FULL-";
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int RANDOM_PART_LENGTH = 8;

    private final KitRepository kitRepository;
    private final KitActivationCodeRepository kitActivationCodeRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<ActivationCodeResponse> generateCodes(GenerateActivationCodesRequest request) {
        String kitCode = normalizeCode(request.getKitCode());

        Kit kit = kitRepository.findByCode(kitCode)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, kitCode));

        if (!Boolean.TRUE.equals(kit.getActive())) {
            throw new RuntimeException("Kit is inactive: " + kitCode);
        }

        List<KitActivationCode> generatedCodes = new ArrayList<>();

        for (int i = 0; i < request.getQuantity(); i++) {
            String code = generateUniqueCode();

            KitActivationCode activationCode = KitActivationCode.builder()
                    .code(code)
                    .kit(kit)
                    .status(ActivationCodeStatus.UNUSED)
                    .usedByUser(null)
                    .usedAt(null)
                    .expiresAt(request.getExpiresAt())
                    .active(true)
                    .note(request.getNote())
                    .build();

            generatedCodes.add(activationCode);
        }

        List<KitActivationCode> savedCodes =
                kitActivationCodeRepository.saveAll(generatedCodes);

        return savedCodes.stream()
                .map(ActivationCodeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<ActivationCodeResponse> getActivationCodes(
            String kitId,
            ActivationCodeStatus status,
            String usedByUserId,
            Pageable pageable
    ) {
        Page<KitActivationCode> page;

        if (kitId != null && status != null) {
            page = kitActivationCodeRepository.findByKitIdAndStatus(
                    kitId,
                    status,
                    pageable
            );
        } else if (kitId != null) {
            page = kitActivationCodeRepository.findByKitId(
                    kitId,
                    pageable
            );
        } else if (status != null) {
            page = kitActivationCodeRepository.findByStatus(
                    status,
                    pageable
            );
        } else if (usedByUserId != null) {
            page = kitActivationCodeRepository.findByUsedByUser_CognitoSub(
                    usedByUserId,
                    pageable
            );
        } else {
            page = kitActivationCodeRepository.findAll(pageable);
        }

        return PageResponseUtils.toPageResponse(page, ActivationCodeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ActivationCodeResponse getByCode(String code) {
        String normalizedCode = normalizeCode(code);

        KitActivationCode activationCode = kitActivationCodeRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new AppException(
                        ActivationCodeErrorCode.ACTIVATION_CODE_NOT_FOUND, normalizedCode
                ));

        return ActivationCodeMapper.toResponse(activationCode);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ActivationCodeResponse updateStatus(
            String id,
            UpdateActivationCodeStatusRequest request
    ) {
        KitActivationCode activationCode = kitActivationCodeRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ActivationCodeErrorCode.ACTIVATION_CODE_NOT_FOUND, id
                ));

        ActivationCodeStatus currentStatus = activationCode.getStatus();
        ActivationCodeStatus newStatus = request.getStatus();

        if (currentStatus == ActivationCodeStatus.USED) {
            throw new AppException(
                    ActivationCodeErrorCode.CANNOT_UPDATE_USED_ACTIVATION_CODE,
                    activationCode.getCode()
            );
        }

        if (newStatus == ActivationCodeStatus.USED) {
            throw new AppException(
                    ActivationCodeErrorCode.CANNOT_MARK_ACTIVATION_CODE_AS_USED_MANUALLY,
                    activationCode.getCode()
            );
        }

        activationCode.setStatus(newStatus);

        if (newStatus == ActivationCodeStatus.LOCKED) {
            activationCode.setActive(false);
        }

        if (newStatus == ActivationCodeStatus.UNUSED) {
            activationCode.setActive(true);

            // Không cần clear usedAt/usedByUser ở đây nữa,
            // vì nếu đã USED thì đã bị chặn bên trên.
            activationCode.setUsedAt(null);
            activationCode.setUsedByUser(null);
        }

        if (newStatus == ActivationCodeStatus.EXPIRED) {
            activationCode.setActive(false);
        }

        KitActivationCode saved = kitActivationCodeRepository.save(activationCode);

        return ActivationCodeMapper.toResponse(saved);
    }

    private String generateUniqueCode() {
        String code;

        do {
            code = CODE_PREFIX + randomString(RANDOM_PART_LENGTH);
        } while (kitActivationCodeRepository.existsByCode(code));

        return code;
    }

    private String randomString(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            builder.append(CHARACTERS.charAt(index));
        }

        return builder.toString();
    }

    private String normalizeCode(String code) {
        if (code == null) {
            throw new RuntimeException("Code must not be null");
        }

        return code.trim().toUpperCase();
    }
}
