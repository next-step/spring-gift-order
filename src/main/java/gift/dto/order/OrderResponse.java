package gift.dto.order;

import gift.domain.Order;

public record OrderResponse(
    Long id,
    Long memberId,
    Long optionId,
    int quantity,
    String message
) {
    public static OrderResponse from(Order order){
        return new OrderResponse(
            order.getId(),
            order.getMember().getId(),
            order.getOption().getId(),
            order.getQuantity(),
            order.getMessage()
        );
    }
}
