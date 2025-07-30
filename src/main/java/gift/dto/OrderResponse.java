package gift.dto;

import gift.domain.Order;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long optionId,
        Integer quantity,
        LocalDateTime orderDateTime,
        String message
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.option().id(),
                order.quantity(),
                order.orderDateTime(),
                order.message()
        );
    }
}
