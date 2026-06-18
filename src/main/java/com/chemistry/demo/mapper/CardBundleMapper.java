package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.chemical.CardBundleResponse;
import com.chemistry.demo.entity.CardBundle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardBundleMapper {
    CardBundleResponse toCardBundleResponse(CardBundle cardBundle);
}
