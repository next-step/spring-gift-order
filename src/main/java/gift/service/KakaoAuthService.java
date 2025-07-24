package gift.service;

import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class KakaoAuthService {
    private final String kakaoRestApiKey;
    private final String redirectUri;
    private final RestTemplate restTemplate;

    public KakaoAuthService(@Value("${kakao_rest_api_key}") String kakaoRestApiKey,
                            @Value("${redirect_uri}") String redirectUri,
                            RestTemplate restTemplate) {
        this.kakaoRestApiKey = kakaoRestApiKey;
        this.redirectUri = redirectUri;
        this.restTemplate = restTemplate;
    }

    public KakaoTokenDto getKakaoToken(String code) {
        var url = "https://kauth.kakao.com/oauth/token";
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoRestApiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
        ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(request, KakaoTokenDto.class);

        return response.getBody();
    }

    public KakaoUserInfoDto getKakaoUserInfo(KakaoTokenDto token) {
        var url = "https://kauth.kakao.com/oauth/userinfo";
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.access_token());

        var request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(request, KakaoUserInfoDto.class);
        return response.getBody();
    }
}
