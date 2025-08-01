package gift.client;

import gift.dto.KakaoLoginResponse;
import gift.dto.KakaoTokenRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoAuthApiClient {

    private final RestClient restClient;

    public KakaoAuthApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .baseUrl("https://kauth.kakao.com")
            .build();
    }

    public KakaoLoginResponse getAccessToken(KakaoTokenRequest request) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", request.grantType());
        body.add("client_id", request.clientId());
        body.add("redirect_uri", request.redirectUri());
        body.add("code", request.code());
        body.add("client_secret", request.clientSecret());

        return restClient.post()
            .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(KakaoLoginResponse.class);
    }
    
}
