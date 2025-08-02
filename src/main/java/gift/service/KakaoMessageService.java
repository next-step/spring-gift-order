package gift.service;

import gift.domain.Member;
import gift.domain.OrderEntity;
import org.springframework.stereotype.Service;

@Service
public class KakaoMessageService {

    public void sendMessageToUser(Member member, OrderEntity order) {
        String message = String.format("""
            [주문 알림]
            옵션: %s
            수량: %d
            메시지: %s
            시간: %s
            """,
                order.getOption().getName(),
                order.getQuantity(),
                order.getMessage(),
                order.getOrderDateTime()
        );

        System.out.println("[카카오톡 메시지 전송]");
        System.out.println("수신자: " + member.getEmail());
        System.out.println(message);
    }
}
