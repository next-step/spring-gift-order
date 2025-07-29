package gift.dto;

import gift.entity.Order;

public record OrderResponse(
    Long id,
    Long productId,
    Long optionId,
    int quantity,
    String message
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getProductOption().getProduct().getId(),
            order.getProductOption().getId(),
            order.getQuantity(),
            order.getMessage()
        );
    }
}
