package gift.client;

import gift.config.KakaoProperties;
import gift.dto.login.KakaoProfileDto;
import gift.dto.login.KakaoTokenDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoClient {

    private final KakaoProperties kakaoProperties;

    private final RestClient restClient;

    public KakaoClient(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
        this.restClient = RestClient.create();
    }

    public KakaoTokenDto fetchToken(String code) {
        String baseUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", code);

        ResponseEntity<KakaoTokenDto> response = restClient.post()
            .uri(baseUrl)
            .headers(h -> h.addAll(headers))
            .body(body)
            .retrieve()
            .toEntity(KakaoTokenDto.class);

        return response.getBody();
    }

    public KakaoProfileDto fetchProfile(String token) {
        String baseUrl = "https://kapi.kakao.com/v2/user/me";
        String bearerToken = "Bearer " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        ResponseEntity<KakaoProfileDto> response = restClient.post()
            .uri(baseUrl)
            .headers(h -> h.addAll(headers))
            .retrieve()
            .toEntity(KakaoProfileDto.class);

        return response.getBody();
    }
}