package gift.dto;

import gift.model.Order;

public record OrderResponseDTO(
        Long id,
        Long optionId,
        int quantity,
        String orderDateTime,
        String message
) {
    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getProductOption().getId(), // Order 엔티티에서 ProductOption의 ID를 가져옵니다.
                order.getQuantity(),
                order.getOrderDateTime().toString(),
                order.getMessage()
        );
    }

}