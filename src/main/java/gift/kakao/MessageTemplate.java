package gift.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.order.OrderEntity;
import java.util.Map;

public class MessageTemplate {


    public static String ofOrder(OrderEntity orderEntity) {
        String orderInfo = String.format("""
                상품 주문이 완료되었습니다.
                상품: %s
                옵션: %s
                수량: %d
                메세지: %s
                """,
            orderEntity.getOption().getItem().getName(),
            orderEntity.getOption().getName(),
            orderEntity.getQuantity(),
            orderEntity.getMessage()
        ).strip();

        Map<String, Object> template = java.util.Map.of(
            "object_type", "text",
            "text", orderInfo,
            "link", Map.of(
                "web_url", "https://developers.kakao.com",
                "mobile_web_url", "https://developers.kakao.com"
            )
        );

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
