package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.request.KakaoLink;
import gift.dto.request.KakaoMessageTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;



@Service
public class KakaoMessageService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessageToMe(String accessToken, String messageText) {
        KakaoLink link = new KakaoLink(
                "https://developers.kakao.com",
                "https://developers.kakao.com"
        );

        KakaoMessageTemplate template = new KakaoMessageTemplate(
                "text",
                messageText,
                link,
                "확인"
        );

        String templateJson;
        try {
            templateJson = objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        restClient.post()
                .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
