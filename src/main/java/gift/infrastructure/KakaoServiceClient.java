package gift.infrastructure;

import gift.dto.KakaoFeedMessageDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoServiceClient {
    private final RestTemplate kakaoKapiRestTemplate;

    public KakaoServiceClient(RestTemplate kakaoKapiRestTemplate) {
        this.kakaoKapiRestTemplate = kakaoKapiRestTemplate;
    }

    public void sendFeedMessageToMe (String accessToken, KakaoFeedMessageDto feedMessageDto) {
        final String url = "/v2/api/talk/memo/default/send";

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var body = new LinkedMultiValueMap<String, KakaoFeedMessageDto>();
        body.add("template_object", feedMessageDto);

        var response = kakaoKapiRestTemplate.postForEntity(url, body, Integer.class, headers);

        System.out.println(response.getBody()); // 디버그용
        System.out.println(response.getStatusCode()); // 디버그용

        if (response.getBody() != 0)
            throw new RuntimeException("메세지가 전송되지 않았습니다.");
    }
}
