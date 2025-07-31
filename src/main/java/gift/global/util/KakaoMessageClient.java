package gift.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakao.KakaoMessageResponse;
import java.time.Duration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoMessageClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public KakaoMessageClient(WebClient.Builder webClientBuilder,
        ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://kapi.kakao.com").build();
        this.objectMapper = objectMapper;
    }

    public KakaoMessageResponse sendOrderMessage(String accessToken, String productName,
        int quantity, String customMessage) {
        String messageSendPath = "/v2/api/talk/memo/default/send";
        String templateObject = createTextTemplate(productName, quantity, customMessage);

        String body = "template_object=" + templateObject;

        return webClient.post()
            .uri(messageSendPath)
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(KakaoMessageResponse.class)
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    private String createTextTemplate(String productName, int quantity, String customMessage) {
        String text = String.format(
            "주문이 완료되었습니다!\n\n"
                + "상품: %s\n"
                + "수량: %d개\n\n"
                + "수령인 메시지: %s\n\n"
                + "감사합니다",
            productName, quantity, customMessage);

        try {
            Link link = new Link("http://localhost:8080/orders", "http://localhost:8080/mobile/orders", null, null);
            TextTemplate textTemplate = new TextTemplate("text", text, link);
            return objectMapper.writeValueAsString(textTemplate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 템플릿 JSON 변환 실패", e);
        }
    }

    private record TextTemplate(String object_type, String text, Link link) {

    }

    private record Link(String web_url, String mobile_web_url, String android_execution_params,
                        String ios_execution_params) {

    }
}
