package gift.client;

import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoClient {

    private final RestClient kauthApiClient;
    private final RestClient kapiApiClient;

    public KakaoClient(@Qualifier("kauthApiClient") RestClient kauthApiClient,
            @Qualifier("kapiApiClient") RestClient kapiApiClient) {
        this.kauthApiClient = kauthApiClient;
        this.kapiApiClient = kapiApiClient;
    }

    public KakaoTokenResponse getKakaoToken(String grantType, String clientId, String redirectUri,
            String code, String clientSecret) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("client_secret", clientSecret);

        return kauthApiClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        return kapiApiClient.post()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }

    public void sendKakaoTalkMessage(String accessToken, String templateObject) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateObject);

        kapiApiClient.post()
                .uri("/v2/api/talk/memo/default/send")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}