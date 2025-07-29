package gift.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMessageService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOrderMessageToMe(String accessToken, String messageText) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String template = """
                {
                  "object_type": "text",
                  "text": "%s",
                  "link": {
                    "web_url": "http://localhost:8080/api/orders"
                  },
                  "button_title": "주문 확인하기"
                }
                """.formatted(messageText);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", template);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
}

