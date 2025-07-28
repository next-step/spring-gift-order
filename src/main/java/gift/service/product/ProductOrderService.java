package gift.service.product;

import gift.dto.order.KakaoOrderResponseDto;

public interface ProductOrderService {

    public KakaoOrderResponseDto sendOrderMessage(Long id, Long memberId, String message,
        int quantity);
}
