package gift.controller;

import gift.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

@Controller
public class KakaoController {

    @Value("${kakao.client-id}")
    private String CLIENT_ID;

    @Value("${kakao.redirect-uri}")
    private String REDIRECT_URI;

    private final RestClient restClient;

    public KakaoController() {
        this.restClient = RestClient.create();
    }

    @GetMapping("/")
    @ResponseBody
    public String handleKakaoCallback(@RequestParam("code") String authorizationCode) {
        try {
            KakaoTokenResponse tokenResponse = requestKakaoToken(authorizationCode);

            StringBuilder result = new StringBuilder();
            result.append("토큰 요청 성공\n");
            result.append("Token Type: ").append(tokenResponse.getTokenType()).append("\n");
            result.append("Access Token: ").append(tokenResponse.getAccessToken()).append("\n");
            result.append("Expires In: ").append(tokenResponse.getExpiresIn()).append(" seconds\n");
            result.append("Refresh Token: ").append(tokenResponse.getRefreshToken()).append("\n");
            result.append("Refresh Token Expires In: ").append(tokenResponse.getRefreshTokenExpiresIn()).append(" seconds\n");

            if (tokenResponse.getIdToken() != null) {
                result.append("ID Token: ").append(tokenResponse.getIdToken()).append("\n");
            }
            if (tokenResponse.getScope() != null) {
                result.append("Scope: ").append(tokenResponse.getScope()).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "토큰 요청 실패: " + e.getMessage();
        }
    }

    private KakaoTokenResponse requestKakaoToken(String authorizationCode) {
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
                .body(KakaoTokenResponse.class);
    }
}
