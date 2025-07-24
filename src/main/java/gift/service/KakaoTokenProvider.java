package gift.service;

import gift.common.exception.KakaoLoginException;
import gift.dto.kakao.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoTokenProvider {

    private final String clientId;

    private final String clientSecret;

    private final RestClient restClient;

    public KakaoTokenProvider(
            @Value("${spring.kakao.client_id}") String clientId,
            @Value("${spring.kakao.client_secret}") String clientSecret,
            RestClient.Builder builder
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = builder.baseUrl("https://kauth.kakao.com/oauth/token").build();
    }

    public KakaoTokenResponse getAccessToken(String code) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("code", code);

            ResponseEntity<KakaoTokenResponse> entity = restClient
                    .post()
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
