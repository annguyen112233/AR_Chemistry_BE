package com.chemistry.demo.config.seeder;

import com.chemistry.demo.entity.CardBundle;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.repository.CardBundleRepository;
import com.chemistry.demo.repository.ChemicalCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class CardBundleSeeder implements DataSeeder {

    private final CardBundleRepository cardBundleRepository;
    private final ChemicalCardRepository chemicalCardRepository;

    @Override
    @Transactional
    public void seed() {
        if (cardBundleRepository.count() > 0) {
            return;
        }

        createBundle(
                "Starter Pack",
                "Basic starter elements",
                BigDecimal.valueOf(9000),
                BigDecimal.valueOf(7200),
                List.of("Na", "Mg", "Cl")
        );

        createBundle(
                "Metal Pack",
                "Popular metal elements",
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(8000),
                List.of("Fe", "Cu", "Ca")
        );
    }

    private void createBundle(
            String name,
            String description,
            BigDecimal originalPrice,
            BigDecimal discountedPrice,
            List<String> symbols
    ) {
        Set<ChemicalCard> cards = new HashSet<>();

        for (String symbol : symbols) {
            ChemicalCard card = chemicalCardRepository.findBySymbol(symbol)
                    .orElseThrow(() -> new RuntimeException("Chemical card not found: " + symbol));
            cards.add(card);
        }

        CardBundle bundle = CardBundle.builder()
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .active(true)
                .purchasable(true)
                .cards(cards)
                .build();

        cardBundleRepository.save(bundle);
    }

    @Override
    public int getOrder() {
        return 7;
    }
}