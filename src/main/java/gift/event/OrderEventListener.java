package gift.event;

import gift.service.kakaoService.KaKaoMessageService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final KaKaoMessageService kaKaoMessageService;

    public OrderEventListener(KaKaoMessageService kaKaoMessageService) {
        this.kaKaoMessageService = kaKaoMessageService;
    }

    @Async
    @EventListener
    public void handleOrderComplete(OrderEvent event) {
        kaKaoMessageService.sendMessage(event.getUserEmail(), event.getMessage());
    }
}
