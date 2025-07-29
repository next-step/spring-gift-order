package gift.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.user.exception.KakaoSendMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;


@Component
public class KakaoMessageClient {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageClient.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public KakaoMessageClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void sendOrderMessageToUser(String accessToken, String productName, String optionName, Long quantity, String message) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        Map<String, Object> template = Map.of(
                "object_type", "text",
                "text", String.format("주문 완료 🎉\n\n상품명: %s\n옵션: %s\n수량: %d개\n\n%s", productName, optionName, quantity, message),
                "link", Map.of("web_url", "https://your-site.com", "mobile_web_url", "https://your-site.com"),
                "button_title", "선물하러 가기"
        );

        try {
            String templateJson = objectMapper.writeValueAsString(template);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("template_object", templateJson);

            restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("카카오 메시지 전송 실패: {}", e.getMessage());
            throw new KakaoSendMessageException("메세지 전송이 실패했습니다.");
        }
    }
}
