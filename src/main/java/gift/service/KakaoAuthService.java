package gift.service;

import gift.auth.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoAuthService {

    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoAuthService(RestClient.Builder restClientBuilder, KakaoProperties kakaoProperties) {
        this.restClient = restClientBuilder.baseUrl("https://kauth.kakao.com").build();
        this.kakaoProperties = kakaoProperties;
    }

    public String getAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", code);

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
