package gift.client;

import gift.auth.KakaoProperties;
import gift.dto.KakaoMessageRequest;
import gift.dto.KakaoTokenResponse;
import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoRestClient implements KakaoApiClient {

    private final RestClient kauthRestClient;
    private final RestClient kapiRestClient;
    private final KakaoProperties kakaoProperties;

    public KakaoRestClient(RestClient.Builder restClientBuilder, KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
            .withConnectTimeout(Duration.ofSeconds(5))
            .withReadTimeout(Duration.ofSeconds(5));
        var requestFactory = ClientHttpRequestFactories.get(settings);

        this.kauthRestClient = restClientBuilder.clone()
            .baseUrl("https://kauth.kakao.com")
            .requestFactory(requestFactory)
            .build();

        this.kapiRestClient = restClientBuilder.clone()
            .baseUrl("https://kapi.kakao.com")
            .requestFactory(requestFactory)
            .build();
    }

    @Override
    public String getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", authorizationCode);

        KakaoTokenResponse response = kauthRestClient.post()
            .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(KakaoTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new RuntimeException("카카오 토큰 발급에 실패했습니다.");
        }
        return response.accessToken();
    }

    @Override
    public void sendMessageToMe(String accessToken, KakaoMessageRequest message) {
        kapiRestClient.post()
            .uri("/v2/api/talk/memo/default/send")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("template_object=" + convertMessageToJson(message))
            .retrieve()
            .toBodilessEntity();
    }

    private String convertMessageToJson(KakaoMessageRequest message) {
        try {
            var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.writeValueAsString(message.getTemplateObject());
        } catch (Exception e) {
            throw new RuntimeException("카카오 메시지 JSON 변환에 실패했습니다.", e);
        }
    }
}
