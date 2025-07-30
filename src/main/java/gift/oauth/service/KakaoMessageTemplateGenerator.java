package gift.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gift.api.option.domain.Option;
import gift.api.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class KakaoMessageTemplateGenerator {

    private final ObjectMapper objectMapper;

    public KakaoMessageTemplateGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String createOrderTemplateAsString(Order order) throws JsonProcessingException {
        ObjectNode linkNode = objectMapper.createObjectNode();
        linkNode.put("web_url", "http://localhost:8080/members/products/"
                + order.getOption().getProduct().getId());

        ObjectNode templateNode = objectMapper.createObjectNode();
        templateNode.put("object_type", "text");
        templateNode.put("text", buildOrderMessageText(order));
        templateNode.set("link", linkNode);
        templateNode.put("button_title", "주문 확인하기");

        return objectMapper.writeValueAsString(templateNode);
    }

    private String buildOrderMessageText(Order order) {
        Option option = order.getOption();

        return String.format(
                "새로운 주문이 완료되었습니다! 🎉\n\n" +
                        "상품명: %s\n" +
                        "옵션: %s\n" +
                        "수량: %d개\n" +
                        "메시지: %s",
                option.getProduct().getName(),
                option.getName(),
                order.getQuantity(),
                order.getMessage() != null ? order.getMessage() : "(메시지 없음)"
        );
    }
}
