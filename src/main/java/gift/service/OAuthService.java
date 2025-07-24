package gift.service;

import gift.config.KakaoOauthProperties;
import gift.dto.kakao.KakaoTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {

    private final KakaoOauthProperties kakaoOauthProperties;

    public OAuthService(KakaoOauthProperties kakaoOauthProperties) {
        this.kakaoOauthProperties = kakaoOauthProperties;
    }

    public KakaoTokenResponse getKakaoToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoOauthProperties.getClientId());
        body.add("redirect_uri", kakaoOauthProperties.getRedirectUri());
        body.add("code", code);
        body.add("client_secret", kakaoOauthProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<KakaoTokenResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                KakaoTokenResponse.class
        );

        return responseEntity.getBody();
    }
}
