package gift.event;

import gift.client.KakaoClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class KakaoSendMessageEventHandler {

    private final KakaoClient kakaoClient;


    public KakaoSendMessageEventHandler(KakaoClient kakaoClient) {
        this.kakaoClient = kakaoClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendKakaoMessage(OrderPlacedEvent event) {
        kakaoClient.sendKakaoMessage(
            event.accessToken(),
            event.message(),
            event.imageUrl()
        );
    }
}