package gift.dto.kakao;

import gift.domain.Order;

import java.time.LocalDateTime;

public record KakaoOrderResponse(Long id, Long optionId, Integer quantity, LocalDateTime orderDateTime, String message) {

    public static KakaoOrderResponse of(Order order, String message) {
        return new KakaoOrderResponse(order.getId(), order.getOptionId(), order.getQuantity(), order.getOrderDateTime(), message);
    }
}
