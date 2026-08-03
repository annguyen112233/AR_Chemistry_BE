package com.chemistry.demo.config.seeder;

import com.chemistry.demo.dto.seed.SeedCompoundItem;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.CompoundDetail;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.CompoundDetailRepository;
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
public class CompoundSeeder implements DataSeeder {

    private final ObjectMapper objectMapper;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;
    private final CompoundDetailRepository compoundDetailRepository;

    @Override
    @Transactional
    public void seed() {
        List<SeedCompoundItem> items = readCompounds();

        for (SeedCompoundItem item : items) {
            String formula = normalizeFormula(item.getFormula());

            ChemicalSubstance substance = chemicalSubstanceRepository
                    .findByFormula(formula)
                    .orElseGet(ChemicalSubstance::new);

            substance.setFormula(formula);
            substance.setName(item.getName());
            substance.setVietnameseName(item.getVietnameseName());
            substance.setType(item.getType());
            substance.setChemicalGroup(item.getChemicalGroup());
            substance.setState(item.getState());
            substance.setMolarMass(item.getMolarMass());
            substance.setActive(item.getActive() != null ? item.getActive() : true);
            substance.setIncludedInFullKit(
                    item.getIncludedInFullKit() != null
                            ? item.getIncludedInFullKit()
                            : false
            );
            substance.setDescription(item.getDescription());
            substance.setSafetyNote(item.getSafetyNote());

            ChemicalSubstance savedSubstance = chemicalSubstanceRepository.save(substance);

            CompoundDetail detail = compoundDetailRepository
                    .findBySubstance_Id(savedSubstance.getId())
                    .orElseGet(CompoundDetail::new);

            detail.setSubstance(savedSubstance);
            detail.setIupacName(item.getIupacName());
            detail.setCasNumber(item.getCasNumber());
            detail.setCompoundClass(item.getCompoundClass());
            detail.setUsageNote(item.getUsageNote());
            detail.setReactionProductOnly(
                    item.getReactionProductOnly() != null
                            ? item.getReactionProductOnly()
                            : false
            );
            detail.setPhysicalInKit(
                    item.getPhysicalInKit() != null
                            ? item.getPhysicalInKit()
                            : Boolean.TRUE.equals(savedSubstance.getIncludedInFullKit())
            );

            compoundDetailRepository.save(detail);
        }
    }

    @Override
    public int getOrder() {
        return 7;
    }

    private List<SeedCompoundItem> readCompounds() {
        try {
            ClassPathResource resource = new ClassPathResource("seed/compounds.json");
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<SeedCompoundItem>>() {}
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read seed/compounds.json", e);
        }
    }

    private String normalizeFormula(String formula) {
        if (formula == null) {
            throw new RuntimeException("Formula must not be null");
        }

        return formula.trim();
    }
}