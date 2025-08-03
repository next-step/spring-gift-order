package gift.order.event.listener;

import gift.auth.service.KakaoMessageService;
import gift.order.event.OrderCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class KakaoOrderListener {
    private final KakaoMessageService kakaoMessageService;

    public KakaoOrderListener(KakaoMessageService kakaoMessageService) {
        this.kakaoMessageService = kakaoMessageService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        kakaoMessageService.sendOrderMemo(event.accessToken(), event.order());
    }
}
