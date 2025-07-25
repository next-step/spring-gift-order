package gift.service;

import gift.dto.KakaoTokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {
    private final String CLIENT_ID;
    private final String REDIRECT_URI;
    private final RestClient restClient;

    public KakaoService(@Value("${kakao.client-id}") String clientId,
        @Value("${kakao.redirect-uri}") String redirectUri) {
        this.CLIENT_ID = clientId;
        this.REDIRECT_URI = redirectUri;
        this.restClient = RestClient.create();
    }

    public KakaoTokenResponseDTO requestKakaoToken(String authorizationCode) {
        final String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", authorizationCode);

        return restClient.post()
            .uri(url)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new IllegalArgumentException("인가 코드가 유효하지 않습니다.");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new IllegalStateException("카카오 서버에 문제가 발생했습니다.");
            })
            .body(KakaoTokenResponseDTO.class);
    }
}
