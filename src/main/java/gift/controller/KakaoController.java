package gift.controller;

import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class KakaoController {

    private final RestTemplate restTemplate;

    @Value("${kakao.client.id}")
    private String clientId;

    public KakaoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String kakaoCallback(@RequestParam String code) {
        // 토큰 받기
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        MultiValueMap<String, String> tokenBody = new LinkedMultiValueMap<>();
        tokenBody.add("grant_type", "authorization_code");
        tokenBody.add("client_id", clientId);
        tokenBody.add("redirect_uri", "http://localhost:8080");
        tokenBody.add("code", code);

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);
        KakaoTokenResponse tokenResponse = restTemplate.postForObject(tokenUrl, tokenRequest, KakaoTokenResponse.class);
        String accessToken = tokenResponse.getAccessToken();

        // 사용자 정보 가져오기
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
        KakaoUserInfoResponse userInfoResponse = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, userInfoRequest, KakaoUserInfoResponse.class
        ).getBody();

        return "로그인 성공 !!!! <br> ID: " + userInfoResponse.getId() + " <br> 닉네임: " + userInfoResponse.getNickname();
    }
}