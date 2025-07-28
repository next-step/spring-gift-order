package gift.client;

import gift.auth.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoRestClient implements KakaoApiClient {

    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoRestClient(RestClient.Builder restClientBuilder, KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
            .withConnectTimeout(Duration.ofSeconds(5))
            .withReadTimeout(Duration.ofSeconds(5));
        this.restClient = restClientBuilder
            .baseUrl("https://kauth.kakao.com")
            .requestFactory(ClientHttpRequestFactories.get(settings))
            .build();
    }

    @Override
    public String getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", authorizationCode);

        KakaoTokenResponse response = restClient.post()
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
}
