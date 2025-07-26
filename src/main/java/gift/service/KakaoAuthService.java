package gift.service;

import gift.config.KakaoProperties;
import gift.dto.response.KaKaoTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


@Service
public class KakaoAuthService {
    private RestClient restClient = RestClient.create();
    private final KakaoProperties kakaoProperties;

    public KakaoAuthService(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    public KaKaoTokenResponseDto requestToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", code);

        return restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KaKaoTokenResponseDto.class);
    }
}
