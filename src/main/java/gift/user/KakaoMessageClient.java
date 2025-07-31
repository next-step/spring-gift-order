package gift.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.user.exception.KakaoSendMessageException;
import gift.user.template.MessageTemplate;
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

    public KakaoMessageClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendOrderMessageToUser(String accessToken, MessageTemplate template) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("template_object", template.create());

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
