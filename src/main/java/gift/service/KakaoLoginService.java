package gift.service;

import gift.config.KakaoProperties;
import gift.dto.TokenResponse;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoLoginService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;

    public KakaoLoginService(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
        this.restTemplate = new RestTemplate();
    }

    public TokenResponse getAccessToken(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "x-www-form-urlencoded",
            java.nio.charset.StandardCharsets.UTF_8));

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", authorizationCode);
        body.add("client_secret", kakaoProperties.clientSecret());

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request,
                Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                throw new RuntimeException("Empty response");
            }

            if (response.getStatusCode().value() != 200) {
                throw new RuntimeException(
                    "Failed with status: " + response.getStatusCode());
            }

            if (!responseBody.containsKey("access_token")) {
                throw new RuntimeException("Access token not found");
            }

            String accessToken = (String) responseBody.get("access_token");
            return new TokenResponse(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token: " + e.getMessage());
        }
    }

}
