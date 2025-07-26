package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenRequestDto;
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
        KakaoTokenRequestDto body = new KakaoTokenRequestDto(
                kakaoProperties.getClientId(),
                kakaoProperties.getRedirectUri(),
                authorizeCode
        );

        KakaoTokenResponseDto response = restClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body.dtoToFormData())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response.accessToken();
    }
}
