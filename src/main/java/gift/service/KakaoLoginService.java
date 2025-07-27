package gift.service;

import gift.dto.KakaoLoginResponse;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoLoginService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public KakaoLoginService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken(String code) {
        var url = "https://kauth.kakao.com/oauth/token";

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
        try {
            ResponseEntity<KakaoLoginResponse> response = restTemplate.postForEntity(url, request,
                    KakaoLoginResponse.class
            );

            return response.getBody().accessToken();
        } catch(HttpClientErrorException e) {
            throw new IllegalArgumentException("잘못된 요청입니다." + e.getResponseBodyAsString());
        } catch(HttpServerErrorException e) {
            throw new RuntimeException("카카오 서버에 문제가 발생하였습니다." + e.getResponseBodyAsString());
        }

    }

}
