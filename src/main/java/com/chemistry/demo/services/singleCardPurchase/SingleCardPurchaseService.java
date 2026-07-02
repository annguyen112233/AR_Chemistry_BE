package com.chemistry.demo.services.singleCardPurchase;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.singleCardPurchase.MySingleCardPurchaseResponse;
import com.chemistry.demo.dto.response.singleCardPurchase.SingleCardPurchaseResponse;
import com.chemistry.demo.entity.User;
import org.springframework.data.domain.Pageable;

public interface SingleCardPurchaseService {
    SingleCardPurchaseResponse buySingleCardWithKnowledgePoint(String singleCardId);

    PageResponse<MySingleCardPurchaseResponse> getMySingleCardPurchases(Pageable pageable);

}
