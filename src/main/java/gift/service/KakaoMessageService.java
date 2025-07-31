package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gift.dto.OrderResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoMessageService {
    private final String MESSAGE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String buildOrderMessage(OrderResponseDto order) {
        return String.format(
                "🧾 주문 정보\n" +
                        "• 주문 ID: %s\n" +
                        "• 옵션 ID: %s\n" +
                        "• 수량: %d\n" +
                        "• 주문 시간: %s\n" +
                        "• 메시지: %s",
                order.id(),
                order.optionId(),
                order.quantity(),
                order.orderDateTime(),
                order.message()
        );
    }

    private ObjectNode buildOrderLink(Long orderId) {
        ObjectNode link = objectMapper.createObjectNode();
        String url = "http://localhost:8080/orders/" + orderId;
        link.put("web_url", url);
        link.put("mobile_web_url", url);
        return link;
    }

    private String toJson(ObjectNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }

    public String buildTextTemplate(OrderResponseDto order) {
        ObjectNode templateObject = objectMapper.createObjectNode();
        templateObject.put("object_type", "text");
        templateObject.put("text", buildOrderMessage(order));
        templateObject.set("link", buildOrderLink(order.id()));

        return toJson(templateObject);
    }

    public void sendKakaoMessage(String accessToken, OrderResponseDto order) {
        String templateJson = buildTextTemplate(order);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        restClient.post()
                .uri(MESSAGE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
