package gift.controller;

import gift.dto.KakaoTokenResponseDTO;
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

    private final String CLIENT_ID;
    private final String REDIRECT_URI;
    private final RestClient restClient;

    public KakaoController(@Value("${kakao.client-id}") String clientId,
                          @Value("${kakao.redirect-uri}") String redirectUri) {
        this.CLIENT_ID = clientId;
        this.REDIRECT_URI = redirectUri;
        this.restClient = RestClient.create();
    }

    @GetMapping("/")
    @ResponseBody
    public String handleKakaoCallback(@RequestParam("code") String authorizationCode) {
        try {
            KakaoTokenResponseDTO tokenResponse = requestKakaoToken(authorizationCode);

            StringBuilder result = new StringBuilder();
            result.append("토큰 요청 성공\n");
            result.append("Token Type: ").append(tokenResponse.tokenType()).append("\n");
            result.append("Access Token: ").append(tokenResponse.accessToken()).append("\n");
            result.append("Expires In: ").append(tokenResponse.expiresIn()).append(" seconds\n");
            result.append("Refresh Token: ").append(tokenResponse.refreshToken()).append("\n");
            result.append("Refresh Token Expires In: ").append(tokenResponse.refreshTokenExpiresIn()).append(" seconds\n");

            if (tokenResponse.idToken() != null) {
                result.append("ID Token: ").append(tokenResponse.idToken()).append("\n");
            }
            if (tokenResponse.scope() != null) {
                result.append("Scope: ").append(tokenResponse.scope()).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "토큰 요청 실패: " + e.getMessage();
        }
    }

    private KakaoTokenResponseDTO requestKakaoToken(String authorizationCode) {
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
                .body(KakaoTokenResponseDTO.class);
    }
}
