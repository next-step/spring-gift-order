package gift.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gift.api.option.domain.Option;
import gift.api.order.domain.Order;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoMessageService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.message-uri}")
    private String messageUri;

    public KakaoMessageService(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    public void sendOrderMessageToMe(String accessToken, Order order) {
        try {
            String templateObject = createOrderTemplateAsString(order);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", templateObject);

            restClient.post()
                    .uri(messageUri)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(new MediaType("application", "x-www-form-urlencoded",
                            StandardCharsets.UTF_8))
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException("카카오 메시지 발송에 실패했습니다." + e);
        }
    }

    private String createOrderTemplateAsString(Order order) throws JsonProcessingException {
        ObjectNode linkNode = objectMapper.createObjectNode();
        linkNode.put("web_url", "http://localhost:8080/members/products/"
                + order.getOption().getProduct().getId());

        ObjectNode templateNode = objectMapper.createObjectNode();
        templateNode.put("object_type", "text");
        templateNode.put("text", buildOrderMessageText(order));
        templateNode.put("link", linkNode);
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
