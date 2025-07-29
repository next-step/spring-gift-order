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
    private final String MessageUrl = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectNode createItem(String item, String itemOp) {
        return objectMapper.createObjectNode()
                .put("item", item)
                .put("item_op", itemOp);
    }

    public String buildFeedTemplate(OrderResponseDto order) {
        ObjectNode content = objectMapper.createObjectNode();
        content.put("title", "주문 정보 일람");
        content.put("description", "주문 ID: " + order.id() + "\n"
                + "옵션 ID: " + order.optionId() + "\n"
                + "수량: " + order.quantity() + "\n"
                + "주문 시간: " + order.orderDateTime() + "\n"
                + "메시지: " + order.message());

        ObjectNode link = objectMapper.createObjectNode();
        link.put("web_url", "http://localhost:8080/orders/" + order.id());
        content.set("link", link);

        ObjectNode templateObject = objectMapper.createObjectNode();
        templateObject.put("object_type", "feed");
        templateObject.set("content", content);

        try {
            return objectMapper.writeValueAsString(templateObject);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }

    public void sendKakaoMessage(String accessToken, OrderResponseDto order) {
        String templateJson = buildFeedTemplate(order); // JSON 문자열로 구성한 템플릿

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        restClient.post()
                .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
