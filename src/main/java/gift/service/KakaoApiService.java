package gift.service;

import gift.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoApiService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient;

    public KakaoApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String getAuthUrl() {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "talk_message,openid,account_email")
                .build()
                .toString();
    }

    public KakaoTokenResponse getToken(String code) {
        LinkedMultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("grant_type", "authorization_code");
        request.add("client_id", clientId);
        request.add("redirect_uri", redirectUri);
        request.add("code", code);

        return restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }
}
