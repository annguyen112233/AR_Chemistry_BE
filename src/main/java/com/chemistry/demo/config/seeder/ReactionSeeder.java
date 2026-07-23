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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReactionSeeder implements DataSeeder {

    private static final String REACTION_SEED_PATH =
            "seed/reactions.json";

    private final ObjectMapper objectMapper;

    private final ChemicalSubstanceRepository
            chemicalSubstanceRepository;

    private final ReactionDefinitionRepository
            reactionDefinitionRepository;

    private final ReactionSubstanceRepository
            reactionSubstanceRepository;

    @Override
    @Transactional
    public void seed() {
        List<SeedReactionItem> items = readReactions();

        for (SeedReactionItem item : items) {
            validateReactionItem(item);

            seedReaction(item);
        }

        log.info(
                "Reaction seeding completed. Total reactions: {}",
                items.size()
        );
    }

    private void seedReaction(SeedReactionItem item) {
        String code = normalizeCode(item.getCode());

        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findByCode(code)
                        .orElseGet(ReactionDefinition::new);

        reaction.setCode(code);
        reaction.setName(item.getName().trim());
        reaction.setEquation(item.getEquation().trim());
        reaction.setReactionType(item.getReactionType());
        reaction.setReactionCategory(
                item.getReactionCategory()
        );
        reaction.setArSceneKey(item.getArSceneKey());
        reaction.setDescription(
                normalizeNullableText(item.getDescription())
        );
        reaction.setScript(
                resolveScript(item)
        );
        reaction.setGrade(item.getGrade());
        reaction.setActive(
                item.getActive() != null
                        ? item.getActive()
                        : true
        );

        ReactionDefinition savedReaction =
                reactionDefinitionRepository.save(reaction);

        /*
         * Xóa reactant/product cũ rồi seed lại.
         * Phù hợp khi JSON là nguồn dữ liệu chuẩn.
         */
        List<ReactionSubstance> oldSubstances =
                reactionSubstanceRepository
                        .findByReactionId(
                                savedReaction.getId()
                        );

        if (!oldSubstances.isEmpty()) {
            reactionSubstanceRepository
                    .deleteAll(oldSubstances);

            reactionSubstanceRepository.flush();
        }

        createReactionSubstances(
                savedReaction,
                item.getReactants(),
                ReactionRole.REACTANT
        );

        createReactionSubstances(
                savedReaction,
                item.getProducts(),
                ReactionRole.PRODUCT
        );

        log.info(
                "Seeded reaction {} with {} reactants and {} products.",
                savedReaction.getCode(),
                sizeOf(item.getReactants()),
                sizeOf(item.getProducts())
        );
    }

    private void createReactionSubstances(
            ReactionDefinition reaction,
            List<SeedReactionSubstanceItem> items,
            ReactionRole role
    ) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int substanceOrder = 1;

        for (SeedReactionSubstanceItem item : items) {
            String formula =
                    normalizeFormula(item.getFormula());

            ChemicalSubstance substance =
                    chemicalSubstanceRepository
                            .findByFormula(formula)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Substance not found when "
                                                    + "seeding reaction "
                                                    + reaction.getCode()
                                                    + ": "
                                                    + formula
                                    )
                            );

            ReactionSubstance reactionSubstance =
                    ReactionSubstance.builder()
                            .reaction(reaction)
                            .substance(substance)
                            .role(role)
                            .coefficient(
                                    item.getCoefficient() != null
                                            ? item.getCoefficient()
                                            : 1
                            )
                            .substanceOrder(
                                    substanceOrder++
                            )
                            .build();

            reactionSubstanceRepository.save(
                    reactionSubstance
            );
        }
    }

    private void validateReactionItem(
            SeedReactionItem item
    ) {
        if (item.getCode() == null
                || item.getCode().isBlank()) {
            throw new IllegalStateException(
                    "Reaction code must not be blank"
            );
        }

        if (item.getName() == null
                || item.getName().isBlank()) {
            throw new IllegalStateException(
                    "Reaction name must not be blank: "
                            + item.getCode()
            );
        }

        if (item.getEquation() == null
                || item.getEquation().isBlank()) {
            throw new IllegalStateException(
                    "Reaction equation must not be blank: "
                            + item.getCode()
            );
        }

        if (item.getReactionType() == null) {
            throw new IllegalStateException(
                    "Reaction type is required: "
                            + item.getCode()
            );
        }

        if (item.getReactionCategory() == null) {
            throw new IllegalStateException(
                    "Reaction category is required: "
                            + item.getCode()
            );
        }

        if (item.getArSceneKey() == null) {
            throw new IllegalStateException(
                    "AR scene key is required: "
                            + item.getCode()
            );
        }

        if (item.getGrade() == null
                || item.getGrade() < 8
                || item.getGrade() > 12) {
            throw new IllegalStateException(
                    "Grade must be from 8 to 12: "
                            + item.getCode()
            );
        }

        int reactantCount =
                sizeOf(item.getReactants());

        /*
         * Hỗ trợ:
         * - 1 reactant: phản ứng nhiệt phân
         * - 2 reactants: phản ứng thông thường
         */
        if (reactantCount < 1
                || reactantCount > 2) {
            throw new IllegalStateException(
                    "Reaction "
                            + item.getCode()
                            + " must contain one or two reactants"
            );
        }

        validateSubstanceItems(
                item.getReactants(),
                item.getCode(),
                ReactionRole.REACTANT
        );

        validateSubstanceItems(
                item.getProducts(),
                item.getCode(),
                ReactionRole.PRODUCT
        );
    }

    private void validateSubstanceItems(
            List<SeedReactionSubstanceItem> items,
            String reactionCode,
            ReactionRole role
    ) {
        if (items == null) {
            return;
        }

        for (SeedReactionSubstanceItem item : items) {
            if (item.getFormula() == null
                    || item.getFormula().isBlank()) {
                throw new IllegalStateException(
                        role.name()
                                + " formula must not be blank in reaction "
                                + reactionCode
                );
            }

            if (item.getCoefficient() != null
                    && item.getCoefficient() <= 0) {
                throw new IllegalStateException(
                        role.name()
                                + " coefficient must be positive in reaction "
                                + reactionCode
                );
            }
        }
    }

    private List<SeedReactionItem> readReactions() {
        try {
            ClassPathResource resource =
                    new ClassPathResource(
                            REACTION_SEED_PATH
                    );

            try (InputStream inputStream =
                         resource.getInputStream()) {

                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<
                                List<SeedReactionItem>
                                >() {
                        }
                );
            }

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to read "
                            + REACTION_SEED_PATH,
                    exception
            );
        }
    }

    private String resolveScript(
            SeedReactionItem item
    ) {
        if (item.getScript() != null
                && !item.getScript().isBlank()) {
            return item.getScript().trim();
        }

        /*
         * Nếu JSON cũ chưa có script,
         * tạm dùng description.
         */
        return normalizeNullableText(
                item.getDescription()
        );
    }

    private String normalizeFormula(
            String formula
    ) {
        if (formula == null
                || formula.isBlank()) {
            throw new IllegalStateException(
                    "Formula must not be blank"
            );
        }

        return formula.trim();
    }

    private String normalizeCode(
            String code
    ) {
        if (code == null
                || code.isBlank()) {
            throw new IllegalStateException(
                    "Reaction code must not be blank"
            );
        }

        return code.trim().toUpperCase();
    }

    private String normalizeNullableText(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }

    private int sizeOf(List<?> items) {
        return items == null
                ? 0
                : items.size();
    }

    @Override
    public int getOrder() {
        /*
         * Phải chạy:
         * - sau ChemicalSubstanceSeeder
         * - trước QuizSeeder
         */
        return 8;
    }
}