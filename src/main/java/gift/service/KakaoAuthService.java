package gift.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URI;

@Service
public class KakaoAuthService {
    private final String kakaoRestApiKey;
    private final String redirectUri;

    public KakaoAuthService(@Value("kakao_rest_api_key") String kakaoRestApiKey, @Value("redirect_uri") String redirectUri) {
        this.kakaoRestApiKey = kakaoRestApiKey;
        this.redirectUri = redirectUri;
    }

    public String getKakaoToken(String code) {
        var url = "https://kauth.kakao.com/oauth/token";
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoRestApiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
        return null;
    }
}
