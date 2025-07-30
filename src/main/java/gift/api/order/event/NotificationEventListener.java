package gift.api.order.event;

import gift.api.member.domain.Member;
import gift.api.order.domain.Order;
import gift.oauth.repository.TokenRepository;
import gift.oauth.service.KakaoMessageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventListener {

    private final KakaoMessageService kakaoMessageService;
    private final TokenRepository tokenRepository;

    public NotificationEventListener(KakaoMessageService kakaoMessageService,
            TokenRepository tokenRepository) {
        this.kakaoMessageService = kakaoMessageService;
        this.tokenRepository = tokenRepository;
    }

    @TransactionalEventListener
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        Member member = event.getOrder().getMember();
        Order order = event.getOrder();

        tokenRepository.findByMemberAndProvider(member, "KAKAO")
                .ifPresent(
                        token -> kakaoMessageService.sendOrderMessageToMe(token.getAccessToken(),
                                order)
                );
    }
}
