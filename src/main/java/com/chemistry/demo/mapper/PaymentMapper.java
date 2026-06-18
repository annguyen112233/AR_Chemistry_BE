package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.payment.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toPaymentResponse(PaymentRequest payment);

}
