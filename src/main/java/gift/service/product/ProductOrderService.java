package gift.service.product;

import gift.dto.order.KakaoOrderResponseDto;

public interface ProductOrderService {

    public void placeOrderAndSendMessage(Long productId, Long productOptionId,
        Long memberId, String message,
        int quantity);
}
