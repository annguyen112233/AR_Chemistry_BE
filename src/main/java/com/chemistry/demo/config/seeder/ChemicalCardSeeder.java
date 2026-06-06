package com.chemistry.demo.config.seeder;

import com.chemistry.demo.dto.seed.SeedChemicalCardItem;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.repository.ChemicalCardRepository;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChemicalCardSeeder implements DataSeeder {

    private final ObjectMapper objectMapper;
    private final ChemicalCardRepository chemicalCardRepository;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;

    @Override
    @Transactional
    public void seed() {
        List<SeedChemicalCardItem> items = readCards();

        for (SeedChemicalCardItem item : items) {
            String formula = normalizeFormula(item.getFormula());
            String cardCode = normalizeCode(item.getCardCode());
            String qrPayload = normalizeQrPayload(item.getQrPayload());

            ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(formula)
                    .orElseThrow(() -> new RuntimeException(
                            "Substance not found when seeding chemical card: " + formula
                    ));

            ChemicalCard card = chemicalCardRepository.findByCardCode(cardCode)
                    .orElseGet(ChemicalCard::new);

            card.setCardCode(cardCode);
            card.setQrPayload(qrPayload);
            card.setSubstance(substance);
            card.setDisplayName(
                    item.getDisplayName() != null && !item.getDisplayName().isBlank()
                            ? item.getDisplayName()
                            : buildDefaultDisplayName(substance)
            );
            card.setImageUrl(item.getImageUrl());
            card.setActive(item.getActive() != null ? item.getActive() : true);

            chemicalCardRepository.save(card);
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }

    private List<SeedChemicalCardItem> readCards() {
        try {
            ClassPathResource resource = new ClassPathResource("seed/chemical-cards.json");
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<>() {
                        }
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read seed/chemical-cards.json", e);
        }
    }

    private String buildDefaultDisplayName(ChemicalSubstance substance) {
        if (substance.getVietnameseName() != null && !substance.getVietnameseName().isBlank()) {
            return substance.getFormula() + " - " + substance.getVietnameseName();
        }

        return substance.getFormula() + " - " + substance.getName();
    }

    private String normalizeCode(String value) {
        if (value == null) {
            throw new RuntimeException("Card code must not be null");
        }
        return value.trim();
    }

    private String normalizeQrPayload(String value) {
        if (value == null) {
            throw new RuntimeException("QR payload must not be null");
        }
        return value.trim();
    }

    private String normalizeFormula(String value) {
        if (value == null) {
            throw new RuntimeException("Formula must not be null");
        }
        return value.trim();
    }
}