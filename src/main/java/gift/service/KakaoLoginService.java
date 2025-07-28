package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoLoginRequest;
import gift.dto.KakaoLoginResponse;
import gift.dto.TokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoLoginService {

    private final KakaoProperties kakaoProperties;
    private final RestClient restClient;

    public KakaoLoginService(KakaoProperties kakaoProperties,
        RestClient.Builder restClientBuilder) {
        this.kakaoProperties = kakaoProperties;
        this.restClient = restClientBuilder
            .baseUrl("https://kauth.kakao.com")
            .build();
    }

    public TokenResponse getAccessToken(KakaoLoginRequest request) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", request.authorizationCode());
        body.add("client_secret", kakaoProperties.clientSecret());

        KakaoLoginResponse response = restClient.post()
            .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(KakaoLoginResponse.class);

        if (response == null) {
            throw new RuntimeException("Empty response");
        }

        if (response.access_token() == null) {
            throw new RuntimeException("Access token not found");
        }

        return new TokenResponse(response.access_token());
    }

}
