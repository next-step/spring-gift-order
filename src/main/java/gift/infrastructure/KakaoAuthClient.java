package gift.infrastructure;

import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class KakaoAuthClient {
    private final String kakaoRestApiKey;
    private final String redirectUri;
    private final RestTemplate restTemplate;

    public KakaoAuthClient(@Value("${kakao_rest_api_key}") String kakaoRestApiKey,
                            @Value("${redirect_uri}") String redirectUri,
                            RestTemplate kakaoAuthRestTemplate) {
        this.kakaoRestApiKey = kakaoRestApiKey;
        this.redirectUri = redirectUri;
        this.restTemplate = kakaoAuthRestTemplate;
    }

    public KakaoTokenDto getKakaoToken(String code) {
        final String url = "/token";

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoRestApiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        var request = new RequestEntity<>(body, HttpMethod.POST, URI.create(url));
        ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(request, KakaoTokenDto.class);

        return response.getBody();
    }

    public KakaoUserInfoDto getKakaoUserInfo(KakaoTokenDto token) {
        final String url = "/userinfo";
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken());

        var request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(request, KakaoUserInfoDto.class);
        return response.getBody();
    }
}
