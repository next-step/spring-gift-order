package gift.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakao.KakaoMessageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoMessageClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public KakaoMessageClient(RestClient.Builder restClientBuilder,
        ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.baseUrl("https://kapi.kakao.com").build();
        this.objectMapper = objectMapper;
    }

    public KakaoMessageResponse sendOrderMessage(String accessToken, String productName,
        int quantity, String customMessage) {
        String messageSendPath = "/v2/api/talk/memo/default/send";
        String templateObject = createTextTemplate(productName, quantity, customMessage);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateObject);

        return restClient.post()
            .uri(messageSendPath)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(KakaoMessageResponse.class);
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
            return objectMapper.writeValueAsString(
                new TextTemplate("text", text,
                    new Link("http://localhost:8080/orders", "http://localhost:8080/mobile/orders",
                        null, null)));
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