package gift.dto.kakao;

import gift.domain.Order;

import java.time.LocalDateTime;

public record KakaoOrderResponse(Long id, Long optionId, Integer quantity, LocalDateTime orderDateTime, String message) {

    public static KakaoOrderResponse from(Order order) {
        return new KakaoOrderResponse(order.getId(), order.getProductOption().getId(), order.getQuantity(), order.getOrderDateTime(), order.getMessage());
    }
}
