package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.entity.Order;
import gift.entity.Product;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final KakaoClient kakaoClient;
    private final ObjectMapper objectMapper;

    public NotificationService(KakaoClient kakaoClient, ObjectMapper objectMapper) {
        this.kakaoClient = kakaoClient;
        this.objectMapper = objectMapper;
    }


    public void sendOrderCompletionNotification(Order order, String kakaoAccessToken) {
        try {
            String commerceTemplate = createKakaoCommerceTemplate(order);
            kakaoClient.sendKakaoTalkMessage(kakaoAccessToken, commerceTemplate);
        } catch (JsonProcessingException e) {
            // 메시지 전송 실패가 전체 주문 실패로 이어지지 않도록 예외를 로깅만 합니다.
            log.error("카카오 메시지 템플릿 생성 또는 전송에 실패했습니다.", e);
        }
    }

    private String createKakaoCommerceTemplate(Order order) throws JsonProcessingException {
        Product product = order.getOption().getProduct();
        Map<String, Object> commerceDetails = Map.of(
                "product_name", product.getName() + " (" + order.getOption().getName() + ")",
                "regular_price", product.getPrice() * order.getQuantity()
        );
        Map<String, Object> content = Map.of(
                "title", "주문이 성공적으로 완료되었습니다.",
                "image_url", product.getImageUrl(),
                "link", Map.of("web_url", "http://localhost:8080", "mobile_web_url",
                        "http://localhost:8080")
        );
        List<Object> buttons = List.of(
                Map.of(
                        "title", "주문 상세 보기",
                        "link", Map.of("web_url", "http://localhost:8080/wishes", "mobile_web_url",
                                "http://localhost:8080/wishes")
                )
        );
        Map<String, Object> template = Map.of(
                "object_type", "commerce",
                "content", content,
                "commerce", commerceDetails,
                "buttons", buttons
        );
        return objectMapper.writeValueAsString(template);
    }
}