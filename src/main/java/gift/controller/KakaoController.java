package gift.controller;

import gift.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class KakaoController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client.id}")
    private String clientId;

    @GetMapping("/")
    public String kakaoCallback(@RequestParam String code) {
        // 토큰 발급
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenBody = new LinkedMultiValueMap<>();
        tokenBody.add("grant_type", "authorization_code");
        tokenBody.add("client_id", clientId);
        tokenBody.add("redirect_uri", "http://localhost:8080");
        tokenBody.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);
        KakaoTokenResponse tokenResponse = restTemplate.postForObject(tokenUrl, tokenRequest, KakaoTokenResponse.class);

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            return "토큰 발급에 실패했습니다.";
        }

        return "액세스 토큰 발급 성공: " + tokenResponse.getAccessToken();
    }
}