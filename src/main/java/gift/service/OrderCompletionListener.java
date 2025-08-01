package gift.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCompletionListener {

    private final KakaoMessageService kakaoMessageService;

    public OrderCompletionListener(KakaoMessageService kakaoMessageService) {
        this.kakaoMessageService = kakaoMessageService;
    }

    @TransactionalEventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {
        String accessToken = event.getAccessToken();
        String message = String.format(
            "<주문 완료> 상품 옵션 ID: %d, 수량: %d, 메시지: %s",
            event.getOrderId(), event.getQuantity(), event.getMessage()
        );
        kakaoMessageService.sendMessage(accessToken, message);
    }

}
