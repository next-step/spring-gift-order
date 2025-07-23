package gift.client;

import gift.config.KakaoProperties;
import gift.dto.login.KakaoProfileDto;
import gift.dto.login.KakaoTokenDto;
import java.net.URI;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoClient {

    private final KakaoProperties kakaoProperties;

    private final RestTemplate restTemplate;

    public KakaoClient(KakaoProperties kakaoProperties, RestTemplateBuilder builder) {
        this.kakaoProperties = kakaoProperties;
        this.restTemplate = builder.build();
    }

    public KakaoTokenDto fetchToken(String code) {
        String baseUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> request = new RequestEntity<>(body, headers,
            HttpMethod.POST, URI.create(baseUrl));

        ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(request,
            KakaoTokenDto.class);

        return response.getBody();
    }

    public KakaoProfileDto fetchProfile(String token) {
        String baseUrl = "https://kapi.kakao.com/v2/user/me";
        String bearerToken = "Bearer " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> request = new RequestEntity<>(headers,
            HttpMethod.POST, URI.create(baseUrl));

        ResponseEntity<KakaoProfileDto> response = restTemplate.exchange(request,
            KakaoProfileDto.class);

        return response.getBody();
    }
}
