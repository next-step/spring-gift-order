package gift.service.Order;

import gift.dto.Order.OrderRequest;
import gift.dto.Order.OrderResponse;
import gift.entity.Member.Member;

public interface OrderService {

    OrderResponse create(Member member, OrderRequest request);
}
