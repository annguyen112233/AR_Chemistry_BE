package com.chemistry.demo.config.seeder;

import com.chemistry.demo.config.seeder.DataSeeder;
import com.chemistry.demo.dto.seed.SeedSingleCardItem;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.SingleCard;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.SingleCardRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SingleCardSeeder implements DataSeeder {

    private final ObjectMapper objectMapper;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;
    private final SingleCardRepository singleCardRepository;

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(15000);
    private static final int DEFAULT_DURATION_DAYS = 30;
    private static final String DEFAULT_GOOGLE_PLAY_PRODUCT_ID = "single_card_15k";

    @Override
    @Transactional
    public void seed() {
        List<SeedSingleCardItem> items = readCards();

        for (SeedSingleCardItem item : items) {
            String formula = normalizeFormula(item.getFormula());
            String code = normalizeCode(item.getCardCode());
            String qrContent = normalizeQrPayload(item.getQrPayload());

            ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(formula)
                    .orElseThrow(() -> new RuntimeException(
                            "Substance not found when seeding single card: " + formula
                    ));

            SingleCard singleCard = singleCardRepository.findByCode(code)
                    .orElseGet(SingleCard::new);

            singleCard.setCode(code);
            singleCard.setSubstance(substance);
            singleCard.setQrContent(qrContent);
            singleCard.setQrS3Key(item.getQrS3Key());

            singleCard.setName(
                    item.getName() != null && !item.getName().isBlank()
                            ? item.getName()
                            : buildDefaultName(substance)
            );

            singleCard.setDescription(
                    item.getDescription() != null && !item.getDescription().isBlank()
                            ? item.getDescription()
                            : buildDefaultDescription(substance)
            );

            singleCard.setPrice(
                    item.getPrice() != null
                            ? item.getPrice()
                            : DEFAULT_PRICE
            );

            singleCard.setDurationDays(
                    item.getDurationDays() != null
                            ? item.getDurationDays()
                            : DEFAULT_DURATION_DAYS
            );

            singleCard.setGooglePlayProductId(
                    item.getGooglePlayProductId() != null && !item.getGooglePlayProductId().isBlank()
                            ? item.getGooglePlayProductId()
                            : DEFAULT_GOOGLE_PLAY_PRODUCT_ID
            );

            singleCard.setActive(item.getActive() != null ? item.getActive() : true);

            singleCardRepository.save(singleCard);
        }
    }

    @Override
    public int getOrder() {
        return 11;
    }

    private List<SeedSingleCardItem> readCards() {
        try {
            ClassPathResource resource = new ClassPathResource("seed/single-cards.json");
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<>() {
                        }
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read seed/single-cards.json", e);
        }
    }

    private String normalizeFormula(String formula) {
        if (formula == null || formula.isBlank()) {
            throw new RuntimeException("Single card formula is required");
        }

        return formula.trim();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new RuntimeException("Single card code is required");
        }

        return code.trim();
    }

    private String normalizeQrPayload(String qrPayload) {
        if (qrPayload == null || qrPayload.isBlank()) {
            throw new RuntimeException("Single card QR payload is required");
        }

        return qrPayload.trim();
    }

    private String buildDefaultName(ChemicalSubstance substance) {
        String displayName = substance.getVietnameseName() != null
                && !substance.getVietnameseName().isBlank()
                ? substance.getVietnameseName()
                : substance.getName();

        return "Card AR " + displayName + " 30 ngày";
    }

    private String buildDefaultDescription(ChemicalSubstance substance) {
        return "Mở quyền quét AR cho "
                + substance.getFormula()
                + " trong 30 ngày.";
    }
}