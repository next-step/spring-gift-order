package gift.dto.orderDto;

import gift.entity.Order;

import java.time.LocalDateTime;

public record OrderResponseDto(Long id, Long optionId, Integer quantity, LocalDateTime orderDateTime, String message) {
    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(order.getId(), order.getItemOption().getId(), order.getQuantity(), LocalDateTime.now(), order.getMessage());
    }
}
