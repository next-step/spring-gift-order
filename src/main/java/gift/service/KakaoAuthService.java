package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoAuthService {
    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoAuthService(KakaoProperties kakaoProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(KAKAO_AUTH_URL)
                .build();
        this.kakaoProperties = kakaoProperties;
    }

    public String getAccessToken(String authorizeCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", authorizeCode);

        KakaoTokenResponseDto response = restClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response.accessToken();
    }
}
