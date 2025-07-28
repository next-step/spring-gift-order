package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.common.exception.KakaoMessageSendException;
import gift.dto.kakao.KakaoSendMessageResultCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class KakaoMessageService {

    private final RestClient restClient;
    private final ObjectMapper mapper;

    public KakaoMessageService(RestClient.Builder builder, ObjectMapper mapper) {
        this.restClient = builder.build();
        this.mapper = mapper;
    }

    public void sendOrderCompleteMessage(String accessToken, String message) {

        ResponseEntity<KakaoSendMessageResultCode> entity;
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", mapper.writeValueAsString(createTextTemplate(message)));

            entity = restClient
                    .post()
                    .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content_Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .body(body)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .toEntity(KakaoSendMessageResultCode.class);

        } catch (Exception e) {
            throw new KakaoMessageSendException(e);
        }

        if (entity.getBody().resultCode() != 0) {
            throw new KakaoMessageSendException();
        }
    }

    private Map<String, Object> createTextTemplate(String message) {
        return Map.of("object_type", "text", "text", message, "link", Map.of("web_url", "http://localhost:8080", "mobile_web_url", "http://localhost:8080"));
    }

}
