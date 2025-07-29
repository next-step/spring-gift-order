package gift.service;

import gift.config.KakaoProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoMessageService {

    private final KakaoProperties kakaoProperties;
    private final RestClient restClient;

    public KakaoMessageService(KakaoProperties kakaoProperties,
        RestClient.Builder restClientBuilder) {
        this.kakaoProperties = kakaoProperties;
        this.restClient = restClientBuilder
            .baseUrl("https://kapi.kakao.com")
            .build();
    }

    public void sendMessage(String accessToken, String message) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", "{\"object_type\":\"text\",\"text\":\"" + message
            + "\",\"link\":{\"web_url\":\"http://localhost:8080\",\"mobile_web_url\":\"http://localhost:8080\"}}");

        ResponseEntity<String> response = restClient.post()
            .uri("/v2/api/talk/memo/default/send")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", "Bearer " + accessToken)
            .body(body)
            .retrieve()
            .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException(
                "Failed to send KakaoTalk message: " + response.getStatusCode());
        }
    }

}
