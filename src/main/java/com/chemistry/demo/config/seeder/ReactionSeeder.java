package com.chemistry.demo.config.seeder;

import com.chemistry.demo.dto.seed.SeedReactionItem;
import com.chemistry.demo.dto.seed.SeedReactionSubstanceItem;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.ReactionSubstance;
import com.chemistry.demo.enums.ReactionRole;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.repository.ReactionSubstanceRepository;
import com.chemistry.demo.utils.ChemicalFormulaUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ReactionSeeder implements DataSeeder {

    private final ObjectMapper objectMapper;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;
    private final ReactionDefinitionRepository reactionDefinitionRepository;
    private final ReactionSubstanceRepository reactionSubstanceRepository;

    @Override
    @Transactional
    public void seed() {
        List<SeedReactionItem> items = readReactions();
        validateUniqueReactantKeys(items);

        for (SeedReactionItem item : items) {
            String code = normalizeCode(item.getCode());
            String reactantKey = buildReactantKey(item);
            ReactionDefinition reaction = reactionDefinitionRepository
                    .findByCode(code)
                    .orElseGet(ReactionDefinition::new);

            reaction.setCode(code);
            reaction.setReactantKey(reactantKey);
            reaction.setName(item.getName());
            reaction.setEquation(item.getEquation());
            reaction.setReactionType(item.getReactionType());
            reaction.setArSceneKey(item.getArSceneKey());
            reaction.setDescription(item.getDescription());
            reaction.setActive(item.getActive() != null ? item.getActive() : true);

            ReactionDefinition savedReaction = reactionDefinitionRepository.save(reaction);

            List<ReactionSubstance> oldSubstances =
                    reactionSubstanceRepository.findByReactionId(savedReaction.getId());

            reactionSubstanceRepository.deleteAll(oldSubstances);

            createReactionSubstances(savedReaction, item.getReactants(), ReactionRole.REACTANT);
            createReactionSubstances(savedReaction, item.getProducts(), ReactionRole.PRODUCT);
        }

        List<String> missingKeys = reactionDefinitionRepository.findAll().stream()
                .filter(reaction -> reaction.getReactantKey() == null || reaction.getReactantKey().isBlank())
                .map(ReactionDefinition::getCode)
                .toList();
        if (!missingKeys.isEmpty()) {
            throw new IllegalStateException(
                    "Reaction rows are missing reactantKey after seeding: " + String.join(", ", missingKeys)
            );
        }
    }

    static void validateUniqueReactantKeys(List<SeedReactionItem> items) {
        Map<String, String> codeByReactantKey = new HashMap<>();

        for (SeedReactionItem item : items) {
            String code = normalizeCode(item.getCode());
            String reactantKey = buildReactantKey(item);
            String previousCode = codeByReactantKey.putIfAbsent(reactantKey, code);
            if (previousCode != null && !previousCode.equals(code)) {
                throw new IllegalStateException(
                        "Duplicate reaction reactantKey '" + reactantKey
                                + "' for " + previousCode + " and " + code
                );
            }
        }
    }

    @Override
    public int getOrder() {
        return 8;
    }

    private void createReactionSubstances(
            ReactionDefinition reaction,
            List<SeedReactionSubstanceItem> items,
            ReactionRole role
    ) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (SeedReactionSubstanceItem item : items) {
            String formula = ChemicalFormulaUtils.normalizeFormula(item.getFormula());

            ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(formula)
                    .orElseThrow(() -> new RuntimeException(
                            "Substance not found when seeding reaction "
                                    + reaction.getCode()
                                    + ": "
                                    + formula
                    ));

            ReactionSubstance reactionSubstance = ReactionSubstance.builder()
                    .reaction(reaction)
                    .substance(substance)
                    .role(role)
                    .coefficient(item.getCoefficient() != null ? item.getCoefficient() : 1)
                    .build();

            reactionSubstanceRepository.save(reactionSubstance);
        }
    }

    private List<SeedReactionItem> readReactions() {
        try {
            ClassPathResource resource = new ClassPathResource("seed/reactions.json");
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<SeedReactionItem>>() {}
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read seed/reactions.json", e);
        }
    }

    private static String buildReactantKey(SeedReactionItem item) {
        if (item.getReactants() == null || item.getReactants().isEmpty()) {
            throw new IllegalStateException("Reaction must have reactants: " + item.getCode());
        }

        return ChemicalFormulaUtils.buildReactantKey(item.getReactants().stream()
                .map(SeedReactionSubstanceItem::getFormula)
                .toList());
    }

    private static String normalizeCode(String code) {
        if (code == null) {
            throw new RuntimeException("Reaction code must not be null");
        }

        return code.trim().toUpperCase();
    }
}
