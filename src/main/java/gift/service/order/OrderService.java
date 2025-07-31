package gift.service.order;

import gift.dto.order.OrderRequest;
import gift.dto.order.OrderResponse;
import gift.entity.member.Member;

public interface OrderService {

    OrderResponse create(Member member, OrderRequest request);
}
