package gift.order.event;

import gift.order.entity.Order;

public record OrderCreatedEvent(
        Long memberId,
        Order order,
        String accessToken
) {
}
