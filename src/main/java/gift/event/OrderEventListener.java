package gift.event;

import gift.service.kakaoService.KaKaoMessageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    private final KaKaoMessageService kaKaoMessageService;

    public OrderEventListener(KaKaoMessageService kaKaoMessageService) {
        this.kaKaoMessageService = kaKaoMessageService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderComplete(OrderEvent event) {
        kaKaoMessageService.sendMessage(event.getUserEmail(), event.getMessage());
    }
}
