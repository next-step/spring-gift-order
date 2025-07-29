package gift.dto;

import gift.entity.Order;

import java.time.Instant;

public record OrderResponse(Long id, Long optionId, Integer quantity, Instant orderDateTime, String message) {

    public static OrderResponse of(Order order) {
        return new OrderResponse(order.getId(), order.getOption().getId(), order.getQuantity(), order.getOrderDateTime(), order.getMessage());
    }
}
