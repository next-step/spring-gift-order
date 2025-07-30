package gift.oauth.service;

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
    private final KakaoMessageTemplateGenerator templateGenerator;

    @Value("${kakao.api.message-uri}")
    private String messageUri;

    public KakaoMessageService(RestClient restClient,
            KakaoMessageTemplateGenerator templateGenerator) {
        this.restClient = restClient;
        this.templateGenerator = templateGenerator;
    }

    public void sendOrderMessageToMe(String accessToken, Order order) {
        try {
            String templateObject = templateGenerator.createOrderTemplateAsString(order);

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
}
