package gift.service;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;

public interface OrderService {

    OrderResponse create(Member member, OrderRequest request);
}
