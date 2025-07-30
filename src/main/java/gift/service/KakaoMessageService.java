package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoMessageRequest;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoMessageService {
    
    private final KakaoProperties kakaoProperties;

    public KakaoMessageService(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    public void sendOrderMessage(String accessToken, String productName, Integer quantity, String message) {
        try {
            KakaoMessageRequest messageRequest = KakaoMessageRequest.createOrderMessage(productName, quantity, message);

            WebClient.create(kakaoProperties.getMessageUrl())
                    .post()
                    .uri("/v2/api/talk/memo/default/send")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("template_object=" + convertToUrlEncoded(messageRequest))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            // 메시지 전송 실패해도 주문은 완료되도록 로그만 기록
            System.err.println("카카오톡 메시지 전송 실패: " + e.getMessage());
        }
    }

    private String convertToUrlEncoded(KakaoMessageRequest request) {
        // 간단한 JSON 변환 (실제로는 ObjectMapper 사용 권장)
        return String.format(
            "{\"object_type\":\"%s\",\"text\":\"%s\",\"link\":{\"web_url\":\"%s\",\"mobile_web_url\":\"%s\"},\"button_title\":\"%s\"}",
            request.templateObject().objectType(),
            request.templateObject().text().replace("\n", "\\n").replace("\"", "\\\""),
            request.templateObject().link().webUrl(),
            request.templateObject().link().mobileWebUrl(),
            request.templateObject().buttonTitle()
        );
    }
}
