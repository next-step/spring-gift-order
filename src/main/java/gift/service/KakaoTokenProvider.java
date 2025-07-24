package gift.service;

import gift.common.exception.KakaoLoginException;
import gift.dto.kakao.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoTokenProvider {

    @Value("${spring.kakao.client_id}")
    private String clientId;

    @Value("${spring.kakao.client_secret}")
    private String clientSecret;

    private static final String GET_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    public KakaoTokenResponse getAccessToken(String code) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("code", code);

            ResponseEntity<KakaoTokenResponse> entity = RestClient.create()
                    .post()
                    .uri(GET_TOKEN_URL)
                    .body(body)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .toEntity(KakaoTokenResponse.class);

            return entity.getBody();

        } catch (Exception e) {
            throw new KakaoLoginException(e);
        }
    }

}
