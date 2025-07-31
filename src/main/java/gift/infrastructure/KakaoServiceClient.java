package gift.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoFeedMessageDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoServiceClient {
    private final ObjectMapper objectMapper;

    private final RestTemplate kakaoKapiRestTemplate;

    public KakaoServiceClient(RestTemplate kakaoKapiRestTemplate, ObjectMapper objectMapper) {
        this.kakaoKapiRestTemplate = kakaoKapiRestTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendFeedMessageToMe (String accessToken, KakaoFeedMessageDto feedMessageDto) {
        final String url = "/v2/api/talk/memo/default/send";
        String json;

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            json = objectMapper
                    .writerWithDefaultPrettyPrinter()      // 보기 좋게 포맷팅
                    .writeValueAsString(feedMessageDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var body = new LinkedMultiValueMap<String, String>();
        body.add("template_object", json);

        var httpentity = new HttpEntity<>(body, headers);

        var response = kakaoKapiRestTemplate.exchange(url, HttpMethod.POST, httpentity, String.class);
    }
}
