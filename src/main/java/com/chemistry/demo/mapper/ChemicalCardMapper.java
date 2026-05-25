package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.entity.ChemicalCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChemicalCardMapper {
    ChemicalCardResponse toChemicalCardResponse(ChemicalCard chemicalCard);
}
