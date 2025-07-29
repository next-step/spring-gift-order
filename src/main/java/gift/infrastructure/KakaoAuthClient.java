package gift.infrastructure;

import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthClient {
    private final String kakaoRestApiKey;
    private final String redirectUri;
    private final String clientSecret;
    private final RestTemplate restTemplate;

    public KakaoAuthClient(@Value("${kakao_rest_api_key}") String kakaoRestApiKey,
                            @Value("${redirect_uri}") String redirectUri,
                            @Value("${client_secret}") String clientSecret,
                            RestTemplate kakaoAuthRestTemplate) {
        this.kakaoRestApiKey = kakaoRestApiKey;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.restTemplate = kakaoAuthRestTemplate;
    }

    public KakaoTokenDto getKakaoToken(String code) {
        final String url = "https://kauth.kakao.com/oauth/token";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoRestApiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("client_secret", clientSecret);

        var httpentity = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(url, HttpMethod.POST, httpentity, KakaoTokenDto.class);
        return response.getBody();
    }

    public KakaoUserInfoDto getKakaoUserInfo(KakaoTokenDto token) {
        final String url = "https://kapi.kakao.com/v1/oidc/userinfo";
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var httpentity = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(url, HttpMethod.GET, httpentity, KakaoUserInfoDto.class);
        return response.getBody();
    }
}
