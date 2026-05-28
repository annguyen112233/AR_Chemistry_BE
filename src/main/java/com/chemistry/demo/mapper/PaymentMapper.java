package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "email", source = "user.email")
    PaymentResponse toPaymentResponse(Payment payment);
}
