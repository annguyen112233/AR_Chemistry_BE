package com.chemistry.demo.config.seeder;

import com.chemistry.demo.dto.seed.SeedKitItem;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.Kit;
import com.chemistry.demo.entity.KitItem;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.KitItemRepository;
import com.chemistry.demo.repository.KitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FullKitSeeder implements DataSeeder {

    private final ObjectMapper objectMapper;
    private final KitRepository kitRepository;
    private final KitItemRepository kitItemRepository;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;

    @Override
    @Transactional
    public void seed() {
        SeedKitItem item = readFullKit();

        String code = normalizeCode(item.getCode());

        Kit kit = kitRepository.findByCode(code)
                .orElseGet(Kit::new);

        kit.setCode(code);
        kit.setName(item.getName());
        kit.setDescription(item.getDescription());
        kit.setPrice(item.getPrice());
        kit.setActive(item.getActive() != null ? item.getActive() : true);

        Kit savedKit = kitRepository.save(kit);

        List<ChemicalSubstance> substances =
                chemicalSubstanceRepository.findAllByIncludedInFullKitTrueAndActiveTrue();

        for (ChemicalSubstance substance : substances) {
            KitItem kitItem = kitItemRepository
                    .findByKitIdAndSubstanceId(savedKit.getId(), substance.getId())
                    .orElse(null);

            if (kitItem == null) {
                kitItem = KitItem.builder()
                        .kit(savedKit)
                        .substance(substance)
                        .quantity(1)
                        .active(true)
                        .build();
            } else {
                kitItem.setActive(true);

                if (kitItem.getQuantity() == null || kitItem.getQuantity() <= 0) {
                    kitItem.setQuantity(1);
                }
            }

            kitItemRepository.save(kitItem);
        }
    }

    @Override
    public int getOrder() {
        return 9;
    }

    private SeedKitItem readFullKit() {
        try {
            ClassPathResource resource = new ClassPathResource("seed/full-kit.json");
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(inputStream, SeedKitItem.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read seed/full-kit.json", e);
        }
    }

    private String normalizeCode(String code) {
        if (code == null) {
            throw new RuntimeException("Kit code must not be null");
        }

        return code.trim().toUpperCase();
    }
}
