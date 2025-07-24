package gift.login.service;

import gift.login.dto.KakaoTokenResponseDto;
import gift.login.exception.KakaoServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {

    private final String clientId;
    private final String redirectUri;

    RestClient client = RestClient.builder().build();

    public KakaoService(
        @Value("${kakao.app.key}") String clientId,
        @Value("${kakao.redirect_uri}") String redirectUri
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    public String getToken(String code) {

        String requestBody = String.format(
            "grant_type=authorization_code&client_id=%s&redirect_uri=%s&code=%s",
            clientId, redirectUri, code
        );

        KakaoTokenResponseDto response = client.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(requestBody)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (httpRequest, httpResponse) -> {
                    throw new IllegalArgumentException("파라미터 오류");
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (httpRequest, httpResponse) -> {
                    throw new KakaoServerException();
                })
            .body(KakaoTokenResponseDto.class);

        return response.accessToken();
    }
}
