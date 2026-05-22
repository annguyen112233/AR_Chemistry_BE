package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(Payment payment);
}
