package gift.service.product;

import gift.dto.order.KakaoOrderResponseDto;

public interface ProductOrderService {

    public KakaoOrderResponseDto sendOrderMessage(Long productId, Long productOptionId,
        Long memberId, String message,
        int quantity);
}
